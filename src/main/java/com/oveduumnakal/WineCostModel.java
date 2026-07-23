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
 * Grand Exchange economics of making wine, from average (guide) prices: the margin on a
 * single wine is the sell price of a jug of wine minus its grape and jug-of-water inputs.
 * A positive margin is profit, a negative margin is a net cost — callers surface the sign.
 */
public final class WineCostModel
{
	private WineCostModel()
	{
	}

	/**
	 * Profit on one wine at the given average prices.
	 *
	 * @param grapePrice average price of grapes
	 * @param waterPrice average price of a jug of water
	 * @param winePrice  average price of a jug of wine
	 * @return jug-of-wine price minus the two inputs; negative means a loss per wine
	 */
	public static int marginPerWine(int grapePrice, int waterPrice, int winePrice)
	{
		return winePrice - grapePrice - waterPrice;
	}

	/**
	 * Total margin across a number of wines.
	 *
	 * @param count      number of wines
	 * @param grapePrice average price of grapes
	 * @param waterPrice average price of a jug of water
	 * @param winePrice  average price of a jug of wine
	 * @return the per-wine margin multiplied by {@code count}
	 */
	public static long totalMargin(int count, int grapePrice, int waterPrice, int winePrice)
	{
		return (long) count * marginPerWine(grapePrice, waterPrice, winePrice);
	}
}
