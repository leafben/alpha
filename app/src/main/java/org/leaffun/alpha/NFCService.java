package org.leaffun.alpha;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.widget.Toast;

public class NFCService extends HostApduService {

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {

        Toast.makeText(this,"接收到"+commandApdu.toString(),Toast.LENGTH_LONG).show();
        String cardId = "70CC1C91";
        return cardId.getBytes();
    }

    @Override
    public void onDeactivated(int reason) {
        Toast.makeText(this,"断开",Toast.LENGTH_LONG).show();
    }
}
