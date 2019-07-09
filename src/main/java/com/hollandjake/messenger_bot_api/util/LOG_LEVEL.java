package com.hollandjake.messenger_bot_api.util;

public enum LOG_LEVEL {
	NOTHING, DEBUG, DEBUG_MESSAGES, EVERYTHING;

	public boolean lessThan(LOG_LEVEL x) {
		return this.ordinal() < x.ordinal();
	}

	public boolean lessThanEqTo(LOG_LEVEL x) {
		return this.ordinal() <= x.ordinal();
	}

	public boolean greaterThan(LOG_LEVEL x) {
		return this.ordinal() > x.ordinal();
	}

	public boolean greaterThanEqTo(LOG_LEVEL x) {
		return this.ordinal() >= x.ordinal();
	}

	public boolean equalTo(LOG_LEVEL x) {
		return this == x;
	}
}
