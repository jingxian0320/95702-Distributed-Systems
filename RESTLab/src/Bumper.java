import javax.ejb.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;
@Path("/bumper")
@Singleton
public class Bumper   {
    private AtomicLong count = new AtomicLong(0);
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @Produces(MediaType.TEXT_PLAIN)
    public String increment(@QueryParam("val") String query) {
        count.incrementAndGet();
        return "success";
    }
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String display() {
        return count.toString();
    }
}
