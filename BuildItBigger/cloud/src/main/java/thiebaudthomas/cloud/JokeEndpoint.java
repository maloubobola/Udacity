/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package thiebaudthomas.cloud;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import thiebaudthomas.jokes.impl.Tell;

/** An endpoint class we are exposing */
@Api(
  name = "myApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "cloud.thiebaudthomas",
    ownerName = "cloud.thiebaudthomas",
    packagePath=""
  )
)
public class JokeEndpoint {
    @ApiMethod(name = "tellAJoke")
    public JokeBean tellAJoke() {
        JokeBean response = new JokeBean();
        response.setData(Tell.a().random().joke());
        return response;
    }
}
