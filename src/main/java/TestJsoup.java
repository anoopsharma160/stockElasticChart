import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TestJsoup {
    public static void main(String[] args) throws IOException {
        Document document=Jsoup.connect("https://www.nseindia.com/live_market/dynaContent/live_watch/option_chain/optionKeys.jsp?symbolCode=-9999&symbol=BANKNIFTY&symbol=BANKNIFTY&instrument=OPTIDX&date=-&segmentLink=17&segmentLink=17")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36").get();
        System.out.println(document.text());
    }
}
