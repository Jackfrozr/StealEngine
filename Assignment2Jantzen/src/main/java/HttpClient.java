

import java.net.URI;

public interface HttpClient {

	public Boolean get(URI request, StringBuilder messageResponse);
}
