package cs2103.v15_1j.jolt.uifeedback;

import cs2103.v15_1j.jolt.ui.MainViewController;

public class AliasDeleteFeedback implements UIFeedback {
	private String alias;

	public AliasDeleteFeedback(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	@Override
	public void execute(MainViewController con) {
		con.showNotification("\"" + alias + "\" has been deleted as an alias.");
	}

	@Override
	public boolean equals(Object t) {
		if (t == null || !(t instanceof AliasDeleteFeedback)) {
			return false;
		}
		AliasDeleteFeedback other = (AliasDeleteFeedback) t;
		return this.alias.equals(other.alias);
	}
}
