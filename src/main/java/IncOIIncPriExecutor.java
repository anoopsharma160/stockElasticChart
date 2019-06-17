import MoneyControlDataFetcher.ESInsertionIncPri;

import java.io.IOException;
import java.text.ParseException;

public class IncOIIncPriExecutor {
    public static void main(String[] args) throws InterruptedException, ParseException, IOException {
        new ESInsertionIncPri().execute(0);
    }
}
