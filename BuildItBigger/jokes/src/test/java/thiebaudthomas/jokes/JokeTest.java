package thiebaudthomas.jokes;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import thiebaudthomas.jokes.impl.Joke;
import thiebaudthomas.jokes.impl.Tell;

/**
 * Created by thiebaudthomas on 05/01/16.
 */
public class JokeTest {
    @Test
    public void testFirstJoke() {
        Assert.assertEquals(Joke.JOKES.get(0), Tell.a().joke());
    }

    @Test
    public void testRandomJoke() {
        Set<String> jokes = new HashSet<>();
        for(int i=0; i<100; i++) {
            jokes.add(Tell.a().random().joke());
        }
        Assert.assertEquals(jokes.size(),Joke.JOKES.size());
    }
}
