// package viewer;

// import javafx.scene.layout.BorderPane;
// import javafx.scene.web.WebEngine;
// import javafx.scene.web.WebView;
// import javafx.concurrent.Worker.State;
// import java.util.function.Consumer;

// public class OAuthWebViewPage extends BorderPane {
//     private final WebView webView = new WebView();

//     public OAuthWebViewPage(String url, String redirectUri, Consumer<String> onRedirect) {
//         System.out.println("InOauthwebview");
//         setCenter(webView);
//         WebEngine engine = webView.getEngine();
//         engine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
//         engine.load("https://example.com");
//         System.out.println(engine);

//         engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
//             System.out.println("WebView navigated to: " + newLoc);
//             if (newLoc.startsWith(redirectUri)) {
//                 System.out.println("Triggering onRedirect with: " + newLoc);
//                 onRedirect.accept(newLoc);
//             }
//         });
//         System.out.println("FinishI");
//     }

//     public WebView getWebView() {
//         return webView;
//     }
// }