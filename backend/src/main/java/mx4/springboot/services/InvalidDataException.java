/*
 * 2020.04.02 - Created
 */
package mx4.springboot.services;

/**
 *
 * @author Ilamah, Osho
 */
//TODO get customised/localized message
public class InvalidDataException extends RuntimeException {

//    public InvalidDataException(String itemId, String type) {
//        this(String.format("Could not find %s with id: %s", type, itemId));
//    }
    
    public InvalidDataException(String message) {
        super(message);
    }
    
}
