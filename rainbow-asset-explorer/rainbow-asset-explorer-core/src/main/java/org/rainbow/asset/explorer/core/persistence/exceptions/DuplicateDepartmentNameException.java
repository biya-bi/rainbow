/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateDepartmentNameException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3030780847531876335L;
	private final String name;

    public DuplicateDepartmentNameException(String name) {
        this(name, String.format("A department with name '%s' already exists.", name));
    }

    public DuplicateDepartmentNameException(String name,  String message) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
