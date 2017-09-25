package sk.henrichg.phoneprofilesplus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class ScannerService extends WakefulIntentService
{
    private Context context;

    static final String EXTRA_SCANNER_TYPE = "scanner_type";

    public ScannerService()
    {
        super("ScannerService");

        // if enabled is true, onStartCommand(Intent, int, int) will return START_REDELIVER_INTENT,
        // so if this process dies before onHandleIntent(Intent) returns, the process will be restarted
        // and the intent redelivered. If multiple Intents have been sent, only the most recent one
        // is guaranteed to be redelivered.
        // -- but restarted service has intent == null??
        setIntentRedelivery(true);
    }

    @SuppressLint("NewApi")
    @Override
    protected void doWakefulWork(Intent intent) {
        CallsCounter.logCounter(context, "ScannerService.doWakefulWork", "ScannerService_doWakefulWork");

        if (intent == null) {
            PPApplication.logE("%%%% ScannerService.onHandleIntent", "intent=null");
            return;
        }

        context = getApplicationContext();

        PPApplication.logE("%%%% ScannerService.onHandleIntent", "-- START ------------");

        String scannerType = intent.getStringExtra(EXTRA_SCANNER_TYPE);
        PPApplication.logE("%%%% ScannerService.onHandleIntent", "scannerType="+scannerType);

        Scanner scanner = new Scanner(context);
        scanner.doScan(scannerType);

        PPApplication.logE("%%%% ScannerService.onHandleIntent", "-- END ------------");

    }

}
