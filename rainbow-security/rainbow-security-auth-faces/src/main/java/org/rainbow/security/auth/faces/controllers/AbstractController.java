package org.rainbow.security.auth.faces.controllers;

import java.io.Serializable;
import java.util.logging.Logger;

public abstract class AbstractController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1572493624091411518L;

	protected static final String FAILURE = "failure";
	protected static final String SUCCESS = "success";
	protected final Logger logger = Logger.getLogger(this.getClass().getName());
}
