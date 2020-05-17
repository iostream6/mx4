/*
 * 2020.04.02 - Created
 */
package mx4.springboot.services;

/**
 *
 * @author Ilamah, Osho
 */
//TODO get customised/localized message
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String itemId, String type) {
        this(String.format("Could not find %s with id: %s", type, itemId));
    }
    
    private ItemNotFoundException(String message) {
        super(message);
    }
    
}
