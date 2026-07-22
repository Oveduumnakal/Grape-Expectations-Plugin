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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

/**
 * RuneLite configuration for the Grape Expectations plugin.
 *
 * <p>Defines the user-facing settings as defaulted {@code @ConfigItem} accessors.
 * Row-level toggles let the user hide any of the four overlay rows (inventory
 * counts, banked XP, level projection, and the fermentation timer). {@link #GROUP}
 * names the persisted config group. Each accessor's {@code name}/{@code description}
 * is the source of truth shown in the settings UI.
 */
@ConfigGroup(GrapeExpectationsConfig.GROUP)
public interface GrapeExpectationsConfig extends Config
{
	/** Persisted config group name for all settings in this plugin. */
	String GROUP = "grapeexpectations";

	@ConfigItem(
			keyName = "showCounts",
			name = "Show inventory counts",
			description = "Row 1: grape, jug of water, and fermenting wine counts.",
			position = 1
	)
	default boolean showCounts()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showBankedXp",
			name = "Show banked XP",
			description = "Row 2: Cooking XP that will be realized once the wine ferments.",
			position = 2
	)
	default boolean showBankedXp()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showLevelProgress",
			name = "Show level progress",
			description = "Row 3: projected level progress bar once the banked XP lands.",
			position = 3
	)
	default boolean showLevelProgress()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showFermentTimer",
			name = "Show ferment timer",
			description = "Row 4: countdown until the current batch finishes fermenting.",
			position = 4
	)
	default boolean showFermentTimer()
	{
		return true;
	}
}
