/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Compact number formatting for the overlay, mirroring the Stockpile plugin's short form:
 * values scale by the largest fitting magnitude with an uppercase suffix and trailing zeros
 * dropped ({@code 28}, {@code 1.7K}, {@code 12.3K}, {@code 1.5M}), and values under 1,000 are
 * shown as grouped digits. Keeps the icon counts, the wines-to-level tag, and the estimated
 * XP readable without widening the fixed box. Stateless utility; cannot be instantiated.
 */
public final class ShortFormat
{
	private ShortFormat()
	{
	}

	private static final NumberFormat GROUPED = NumberFormat.getIntegerInstance(Locale.US);

	/** Compact form to at most 3 significant figures: {@code 234K}, {@code 2.34K}, {@code 1.5M}. */
	public static String value(long value)
	{
		long abs = Math.abs(value);
		String sign = value < 0 ? "-" : "";

		if (abs >= 1_000_000_000L)
			return sign + mantissa(abs / 1_000_000_000.0) + "B";
		else if (abs >= 1_000_000L)
			return sign + mantissa(abs / 1_000_000.0) + "M";
		else if (abs >= 1_000L)
			return sign + mantissa(abs / 1_000.0) + "K";

		return sign + GROUPED.format(abs);
	}

	/**
	 * Formats a scaled mantissa in {@code [1, 1000)} to 3 significant figures, dropping any
	 * trailing zeros and a dangling decimal point.
	 *
	 * @param d the scaled value in {@code [1, 1000)}
	 * @return the trimmed mantissa string
	 */
	private static String mantissa(double d)
	{
		String s;

		if (d >= 100)
			s = String.format(Locale.US, "%.0f", d);
		else if (d >= 10)
			s = String.format(Locale.US, "%.1f", d);
		else
			s = String.format(Locale.US, "%.2f", d);

		if (s.contains("."))
		{
			s = s.replaceAll("0+$", "");

			if (s.endsWith("."))
				s = s.substring(0, s.length() - 1);
		}

		return s;
	}
}
