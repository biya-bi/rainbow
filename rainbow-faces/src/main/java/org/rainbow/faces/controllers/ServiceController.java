package org.rainbow.faces.controllers;

import org.rainbow.service.services.Service;

public interface ServiceController<T> {
	Service<T> getService();
}
