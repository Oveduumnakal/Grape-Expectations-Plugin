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

import java.awt.Color;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

/**
 * RuneLite configuration for the Grape Expectations plugin.
 *
 * <p>Settings are split into two {@code @ConfigSection}s: which of the four overlay rows to
 * show, and appearance (the estimated-XP text and bar colours). {@link #GROUP} names
 * the persisted config group. The overlay's on-screen position is set by dragging it (RuneLite
 * persists that natively), so it is not a config item. Each accessor's {@code name}/
 * {@code description} is the source of truth shown in the settings UI.
 */
@ConfigGroup(GrapeExpectationsConfig.GROUP)
public interface GrapeExpectationsConfig extends Config
{
	/** Persisted config group name for all settings in this plugin. */
	String GROUP = "grapeexpectations";

	@ConfigSection(
			name = "Rows",
			description = "Which of the four overlay rows to show",
			position = 0
	)
	String rowsSection = "rows";

	@ConfigSection(
			name = "Appearance",
			description = "Overlay text and bar colours",
			position = 1
	)
	String appearanceSection = "appearance";

	@ConfigItem(
			keyName = "showCounts",
			name = "Show inventory counts",
			description = "Row 1: grape, jug of water, and fermenting wine counts.",
			section = rowsSection,
			position = 1
	)
	default boolean showCounts()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showLevelProgress",
			name = "Show level progress",
			description = "Row 2: projected level bar with percent and wines to the next level.",
			section = rowsSection,
			position = 2
	)
	default boolean showLevelProgress()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showBankedXp",
			name = "Show estimated XP",
			description = "Row 3: estimated Cooking XP that will be realized once the wine ferments.",
			section = rowsSection,
			position = 3
	)
	default boolean showBankedXp()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showFermentTimer",
			name = "Show ferment timer",
			description = "Row 4: countdown until the current batch finishes fermenting.",
			section = rowsSection,
			position = 4
	)
	default boolean showFermentTimer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "bankedXpColor",
			name = "Est. XP colour",
			description = "Colour of the estimated-XP text row.",
			section = appearanceSection,
			position = 2
	)
	default Color bankedXpColor()
	{
		return new Color(255, 193, 87);
	}

	@ConfigItem(
			keyName = "levelBarColor",
			name = "Level bar colour",
			description = "Fill colour of the row 3 level progress bar.",
			section = appearanceSection,
			position = 3
	)
	default Color levelBarColor()
	{
		return new Color(70, 130, 200);
	}

	@ConfigItem(
			keyName = "timerBarColor",
			name = "Timer bar colour",
			description = "Fill colour of the row 4 ferment countdown bar.",
			section = appearanceSection,
			position = 4
	)
	default Color timerBarColor()
	{
		return new Color(80, 170, 80);
	}
}
