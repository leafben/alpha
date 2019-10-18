package org.leaffun.alpha;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * NFC读取
 */
public class NFCActivity extends AppCompatActivity {
    private final String TAG = NFCActivity.class.getSimpleName();
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilter;
    private String[][] mTechList;
    private TextView mTvView;

    // 卡片返回来的正确信号
    private final byte[] SELECT_OK = stringToBytes("1000");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        initView();

        readCard(getIntent());

        nfcCheck();
        openReading();
    }

    private void readCard(Intent intent) {
        try {
            Log.e("nfc卡读取", "开始");
            String s = readNFCFromTag(intent);
            Log.e("nfc卡读取标签", "值："+s);



            //70CC1C91
            String s1 = readNFCId(intent);
            Log.e("nfc卡读取ID", "值："+s1);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("nfc卡读取", "错误");
        }

    }


    /**
     * 读取nfcID
     */
    public static String readNFCId(Intent intent) throws UnsupportedEncodingException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag!=null) {
            String id = ByteArrayToHexString(tag.getId());
            return id;
        }else{
            return "";
        }
    }

    /**
     * 将字节数组转换为字符串
     */
    private static String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    /**
     * 读取NFC的数据
     */
    public String readNFCFromTag(Intent intent) throws UnsupportedEncodingException {
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawArray != null) {
            NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
            NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
            if (mNdefRecord != null) {
                String readResult = new String(mNdefRecord.getPayload(), "UTF-8");
                return readResult;
            }
        }else{
            mTvView.setText("rawArray==null");
        }
        return "";
    }


    private void initView() {
        mTvView = (TextView) findViewById(R.id.nfc_activity_tv_info);
    }

    /**
     * 初始化
     */
    private void openReading() {
        // NFCActivity 一般设置为: SingleTop模式 ，并且锁死竖屏，以避免屏幕旋转Intent丢失
        Intent intent = new Intent(NFCActivity.this, NFCActivity.class);

        // 私有的请求码
        final int REQUEST_CODE = 1 << 16;

        final int FLAG = 0;
        mPendingIntent = PendingIntent.getActivity(NFCActivity.this, REQUEST_CODE, intent, FLAG);

        // 三种过滤器
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mIntentFilter = new IntentFilter[]{ndef, tech, tag};

        // 只针对ACTION_TECH_DISCOVERED
        mTechList = new String[][]{
                {IsoDep.class.getName()}, {NfcA.class.getName()}, {NfcB.class.getName()},
                {NfcV.class.getName()}, {NfcF.class.getName()}, {Ndef.class.getName()}};
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        readCard(intent);

        sendCommand(intent);


    }

    private void sendCommand(Intent intent) {

        // IsoDep卡片通信的工具类，Tag就是卡
        IsoDep isoDep = IsoDep.get((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        if (isoDep == null) {
            String info = "读取卡信息失败";
            toast(info);
            return;
        }
        try {
            // NFC与卡进行连接
            isoDep.connect();

            final String AID = "F0010203040506";
            //转换指令为byte[]
            byte[] command = buildSelectApdu(AID);

            // 发送指令
            byte[] result = isoDep.transceive(command);

            // 截取响应数据
            int resultLength = result.length;
            byte[] statusWord = {result[resultLength - 2], result[resultLength - 1]};
            byte[] payload = Arrays.copyOf(result, resultLength - 2);

            // 检验响应数据
            if (Arrays.equals(SELECT_OK, statusWord)) {
                String accountNumber = new String(payload, "UTF-8");
                Log.e(TAG, "----> " + accountNumber);
                mTvView.setText(accountNumber);
            } else {
                String info = bytesToString(result);
                Log.e(TAG, "----> error" + info);
                mTvView.setText(info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启检测,检测到卡后，onNewIntent() 执行
     * enableForegroundDispatch()只能在onResume() 方法中，否则会报：
     * Foreground dispatch can only be enabled when your activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter == null) return;
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilter, mTechList);
    }

    /**
     * 关闭检测
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter == null) return;
        mNfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * 检测是否支持 NFCService
     */
    private void nfcCheck() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            String info = "手机不支付NFC功能";
            toast(info);
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            String info = "手机NFC功能没有打开";
            toast(info);
            Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(setNfc);
        } else {
            String info = "手机NFC功能正常";
            toast(info);
        }
    }

    private byte[] stringToBytes(String s) {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("指令字符串长度必须为偶数 !!!");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[(i / 2)] = ((byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16)));
        }
        return data;
    }

    private String bytesToString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte d : data) {
            sb.append(String.format("%02X", d));
        }
        return sb.toString();
    }


    private byte[] buildSelectApdu(String aid) {
        final String HEADER = "00A40400";
        return stringToBytes(HEADER + String.format("%02X", aid.length() / 2) + aid);
    }

    private void toast(String info) {
        Toast.makeText(NFCActivity.this, info, Toast.LENGTH_SHORT).show();
    }
}