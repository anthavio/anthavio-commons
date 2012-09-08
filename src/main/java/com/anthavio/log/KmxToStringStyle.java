package com.anthavio.log;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Specificky {@link ToStringStyle} pro moznosti lepsi konfigurace. Tento styl
 * vychazi z {@link ToStringStyle#MULTI_LINE_STYLE}, jedina zmena je v nastaveni
 * property ArrayContentDetai na false, coz misto kompletniho vypisu
 * pole vypise pouze jeho velikost
 * 
 * @author janousekm
 */
@SuppressWarnings("serial")
public class KmxToStringStyle extends ToStringStyle {

	public KmxToStringStyle() {
		super();
		this.setContentStart("[");
		this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
		this.setFieldSeparatorAtStart(true);
		this.setContentEnd(SystemUtils.LINE_SEPARATOR + "]");
		this.setArrayContentDetail(true);
	}
}