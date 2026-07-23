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

import lombok.Value;

/**
 * Immutable snapshot of the wine-relevant inventory counts driving the overlay:
 * grapes and jugs of water (row 1 inputs), unfermented wine (the fermenting batch
 * that drives rows 2–4), and finished jugs of wine.
 */
@Value
public class WineTally
{
	/** A tally with every count at zero, used before the inventory is first read. */
	public static final WineTally EMPTY = new WineTally(0, 0, 0, 0);

	int grapes;
	int jugsOfWater;
	int unfermentedWine;
	int jugsOfWine;

	/**
	 * Whether a batch is currently fermenting (i.e. any unfermented wine is held),
	 * which is the condition for showing the banked-XP and timer rows.
	 *
	 * @return true if at least one unfermented wine is present
	 */
	public boolean isFermenting()
	{
		return unfermentedWine > 0;
	}

	/**
	 * A copy of this tally with the unfermented-wine count zeroed, leaving the other counts
	 * intact. Used when the ferment timer expires to drop a batch that finished in the bank
	 * (which fires no container event while the bank is closed) without disturbing the
	 * grape, water, and finished-wine counts.
	 *
	 * @return a tally identical to this one but with zero unfermented wine
	 */
	public WineTally withoutUnfermented()
	{
		return new WineTally(grapes, jugsOfWater, 0, jugsOfWine);
	}
}
