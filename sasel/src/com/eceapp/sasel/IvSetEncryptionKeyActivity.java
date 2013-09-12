package com.eceapp.sasel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class IvSetEncryptionKeyActivity extends Activity {

	/*Activity that changes the IV and Encryption keys.
	 * Checks if the keys match the required length, if not an error is 
	 * set in their entry containers. If they match the specified length, the 
	 * results are saved
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iv_set_encryption_key);
		
		//Button to enable saving of the new IV and Encryption keys
		Button saveIVEncryptionKeys = (Button)findViewById(R.id.save_iv_encryption_key);
		saveIVEncryptionKeys.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				try{
					String iv_key = "";
					String encryptionKey = "";
					String iv_key2 = "";
					String encryptionKey2 = "";
					boolean invalidInput = false;

					EditText iv_et = (EditText)findViewById(R.id.iv_et);					
					//System.err.println("iv_et is null "+(iv_et == null));
					if (iv_et != null){
						iv_key = (iv_et.getText()).toString();
						
						if ((iv_key == null) || ( iv_key.length() != CipherAlgo.REQ_IV_LEN)){
							iv_et.setError("IV Key length must be "+CipherAlgo.REQ_ENCRYPTION_LEN);
							invalidInput = true;
						}
					}

					EditText iv_et2 = (EditText)findViewById(R.id.iv_et2);					
					//System.err.println("iv_et2 is null "+(iv_et2 == null));
					if (iv_et2 != null){
						iv_key2 = (iv_et2.getText()).toString();
						
						if ((iv_key2 == null) || ( iv_key2.length() != CipherAlgo.REQ_IV_LEN)){
							iv_et2.setError("IV Key length must be "+CipherAlgo.REQ_ENCRYPTION_LEN);
							invalidInput = true;
						}
						System.err.println("encryptionKey= "+encryptionKey+" and encryptionKey2 = "+encryptionKey2);
						if ( !iv_key.equals(iv_key2) ){
							iv_et2.setError("IV Keys entries does not match = "+iv_key2);
							invalidInput = true;
						}
					}

					EditText encryptionKey_et = (EditText)findViewById(R.id.encryption_key_et);					
					//System.err.println("encryption_key_et is null "+(encryption_key_et == null));
					if (encryptionKey_et != null){
						encryptionKey = (encryptionKey_et.getText()).toString();
						if ((encryptionKey == null ) || (encryptionKey.length() != CipherAlgo.REQ_ENCRYPTION_LEN)){
							encryptionKey_et.setError("Encryption key length must be "+CipherAlgo.REQ_ENCRYPTION_LEN);
							invalidInput = true;
						}
					}
					
					EditText encryptionKey_et2 = (EditText)findViewById(R.id.encryption_key_et2);					
					//System.err.println("encryption_key_et2 is null "+(encryption_key_et2 == null));
					if (encryptionKey_et2 != null){
						encryptionKey2 = (encryptionKey_et2.getText()).toString();
						if ((encryptionKey2 == null ) || (encryptionKey2.length() != CipherAlgo.REQ_ENCRYPTION_LEN)){
							encryptionKey_et2.setError("Encryption key length must be "+CipherAlgo.REQ_ENCRYPTION_LEN);
							invalidInput = true;
						}
						//System.err.println("encryptionKey= "+encryptionKey+" and encryptionKey2 = "+encryptionKey2);
						if ( !encryptionKey.equals(encryptionKey2) ){
							encryptionKey_et2.setError("Encryption Keys entries does not match" + encryptionKey2);
							invalidInput = true;
						}
					}
					
					if (invalidInput)
						return;
					
					//Sending the results back to the parentIntent
	
					Intent parentIntent = new Intent();
					
					parentIntent.putExtra(Preparation.ENCRYPTION_KEY, encryptionKey);
					parentIntent.putExtra(Preparation.IV_KEY, iv_key);
					setResult(RESULT_OK,parentIntent);
					finish();
				}catch(Exception e){
					e.printStackTrace();
					Toast.makeText(v.getContext(),e.toString(),Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.iv_set_encryption_key, menu);
		return true;
	}

}
