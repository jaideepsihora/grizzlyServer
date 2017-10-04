package grizzlyServer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.http.server.accesslog.AccessLogBuilder;
import org.glassfish.grizzly.http.server.accesslog.AccessLogProbe;
import org.glassfish.grizzly.http.server.accesslog.ApacheLogFormat;

public class GrizzlyServerMain {
	
	
	public static void main(String[] args) {
		
		HttpServer httpServer = HttpServer.createSimpleServer();
		httpServer.getServerConfiguration().addHttpHandler(
					new HttpHandler() {
						
						@Override
						public void service(Request request, Response response) throws Exception {
				            final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
				            final String date = format.format(new Date(System.currentTimeMillis()));
				            response.setContentType("text/plain");
				            response.setContentLength(date.length());
				            response.getWriter().write(date);
						}
					},
					"/time");
		
		try {
			
			enableAccessLog(httpServer);
			StaticHttpHandler staticHttpHandler = new StaticHttpHandler("/var/citruspay/www/");
		    httpServer.getServerConfiguration().addHttpHandler(staticHttpHandler, "/akamai/");
			httpServer.start();
		    System.out.println("Press any key to stop the server...");
		    System.in.read();
		} catch (Exception e) {
		    System.err.println(e);
		}
	}
	
private static void enableAccessLog(HttpServer httpServer) {
		
		AccessLogBuilder builder = new AccessLogBuilder("/tmp/access.log");
		builder.instrument(httpServer.getServerConfiguration());
		AccessLogProbe p1 = builder.format(ApacheLogFormat.COMBINED)
		.build();
		
		ServerConfiguration sc1 = httpServer.getServerConfiguration();
		sc1.getMonitoringConfig().getWebServerConfig().addProbes(p1);
		
		System.out.println(sc1.getMonitoringConfig().getWebServerConfig());
	}
	

}
