package thiebaudthomas.jokes;

import thiebaudthomas.jokes.impl.Tell;

/**
 * Created by thiebaudthomas on 05/01/16.
 */
public interface RandomInterface extends TellInterface {
        TellInterface random();
}
