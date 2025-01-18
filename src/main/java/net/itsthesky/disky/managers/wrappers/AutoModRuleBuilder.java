package net.itsthesky.disky.managers.wrappers;

import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.automod.build.AutoModRuleData;

public class AutoModRuleBuilder {

	private AutoModRuleData autoModRule;

	public AutoModRuleBuilder() {
		this.autoModRule = null;
	}

	public void setAutoModRule(AutoModRuleData autoModRule) {
		this.autoModRule = autoModRule;
	}

	public void addResponses(AutoModResponse... response) {
		if (autoModRule == null) return;
		autoModRule.putResponses(response);
	}

	@Override
	public String toString() {
		return "automod rule";
	}

	public AutoModRuleData build() {
		return autoModRule;
	}
}
