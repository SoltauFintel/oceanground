package de.mwvb.auth.schild;

import de.mwvb.maja.auth.AuthFeature;
import de.mwvb.maja.auth.AuthPlugin;
import de.mwvb.maja.auth.Authorization;
import de.mwvb.maja.auth.BaseAuthorization;
import de.mwvb.maja.auth.rememberme.RememberMeFeature;

public class SchildAuthPlugin extends AuthPlugin {

	public SchildAuthPlugin(RememberMeFeature rem) {
		super(rem);
	}
	
	@Override
	protected AuthFeature getFeature() {
		SchildAuthFeature feature = new SchildAuthFeature();
		feature.init(this);
		return feature;
	}
	
	@Override
	protected Authorization getAuthorization() {
		return new BaseAuthorization() {
			@Override
			protected String getService() {
				return "Schild";
			}
		};
	}
}
