/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oveduumnakal;

/**
 * Heuristic countdown to fermentation for overlay row 4.
 *
 * <p>The game exposes no fermentation varbit, so the timer is driven off observed
 * events: it is {@link #reset(int) reset} to the full {@value #DURATION_TICKS}-tick window
 * whenever a fresh unfermented wine is detected (matching the in-game behaviour where
 * making another wine restarts the timer), and {@link #clear() cleared} once the batch
 * converts. All times are expressed in game ticks; the caller supplies the current tick so
 * the timer stays a pure, testable value object.
 */
public class FermentTimer
{
	/** Fermentation window: 22 game ticks (~13.2 seconds). */
	public static final int DURATION_TICKS = 22;

	private static final double TICK_SECONDS = 0.6;

	private final int durationTicks;
	private boolean active;
	private int endTick;

	public FermentTimer()
	{
		this(DURATION_TICKS);
	}

	public FermentTimer(int durationTicks)
	{
		this.durationTicks = durationTicks;
	}

	/**
	 * (Re)starts the countdown from the given tick — call when a new unfermented wine appears.
	 *
	 * @param currentTick the game tick at which the wine was created
	 */
	public void reset(int currentTick)
	{
		active = true;
		endTick = currentTick + durationTicks;
	}

	/** Stops the countdown — call once the batch has fermented or the state is cleared. */
	public void clear()
	{
		active = false;
		endTick = 0;
	}

	/**
	 * Whether a countdown is currently running.
	 *
	 * @return true between a {@link #reset(int)} and a {@link #clear()}
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 * Ticks left until fermentation, clamped at zero.
	 *
	 * @param currentTick the current game tick
	 * @return remaining ticks, or 0 when inactive or already elapsed
	 */
	public int remainingTicks(int currentTick)
	{
		if (!active)
			return 0;

		return Math.max(0, endTick - currentTick);
	}

	/**
	 * Seconds left until fermentation, for the row 4 label.
	 *
	 * @param currentTick the current game tick
	 * @return remaining seconds (a tick is 0.6s), or 0 when inactive or elapsed
	 */
	public double remainingSeconds(int currentTick)
	{
		return remainingTicks(currentTick) * TICK_SECONDS;
	}

	/**
	 * Fraction of the window still remaining, for the decreasing row 4 bar.
	 *
	 * @param currentTick the current game tick
	 * @return a value in [0, 1]; 0 when inactive or elapsed
	 */
	public double fraction(int currentTick)
	{
		if (!active || durationTicks <= 0)
			return 0.0;

		return Math.max(0.0, Math.min(1.0, remainingTicks(currentTick) / (double) durationTicks));
	}
}
