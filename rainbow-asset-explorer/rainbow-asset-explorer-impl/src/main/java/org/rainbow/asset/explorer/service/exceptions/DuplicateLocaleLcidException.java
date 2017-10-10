package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateLocaleLcidException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3619648671629503614L;
	private final String lcid;

    public DuplicateLocaleLcidException(String lcid) {
        this(lcid, String.format("A locale with LCID '%s' already exists.", lcid));
    }

    public DuplicateLocaleLcidException(String name,  String message) {
        super(message);
        this.lcid = name;
    }

    public String getLcid() {
        return lcid;
    }
}
