

import java.net.URI;
import java.util.List;

public interface HttpParser {
	public List<URI> parseLinks(String content, String url);
}
