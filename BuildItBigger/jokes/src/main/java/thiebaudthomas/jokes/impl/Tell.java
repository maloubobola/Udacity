package thiebaudthomas.jokes.impl;
import java.util.Random;

import thiebaudthomas.jokes.RandomInterface;
import thiebaudthomas.jokes.TellInterface;

public class Tell implements RandomInterface, TellInterface {
    private int index = -1;

    private Tell() {}

    public static RandomInterface a() {
        return new Tell();
    }

    public String joke() {
        String joke = "";

        if (index == -1)
            joke = Joke.JOKES.get(Joke.FIRST_INDEX);
        else
            joke = Joke.JOKES.get(index);

        return joke;
    }
    @Override
    public TellInterface random() {
        this.index = new Random().nextInt(Joke.LAST_INDEX - Joke.FIRST_INDEX) + Joke.FIRST_INDEX;
        return this;
    }
}
