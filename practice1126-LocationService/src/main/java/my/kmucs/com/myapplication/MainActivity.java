package my.kmucs.com.myapplication;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity {

    GoogleMap gmap;
    boolean locationTag = true;

    LocationManager locationManager;
    LocationListener locationListener;
    Button getLocation;
    TextView tv_location, tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gmap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();




        //위치 관리자 얻기
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        getLocation = (Button)findViewById(R.id.getLocation);
        tv_location = (TextView)findViewById(R.id.tv_location);
        tv_address = (TextView)findViewById(R.id.tv_address);


        //제공자 목록 출력하기
        //위치적으로 받아올수있게 미리 받아오는객체들 = provider
        //어떤것들(위치센서, 네트워크, 등)이 켜져있는지 확인해보려고
        //api = 23은 이코드가 안됌, api = 16은 이코드가 가능... 이유는 권한문제.
        List<String> apProvider = locationManager.getProviders(false);
        String result = "";
        for(int i=0 ; i< apProvider.size(); i++){
            result += ("Provider " + i + " : " + apProvider.get(i) + "\n");
        }


        //GPS와 네트워크 제공자를 사용가능한지 검사 : GPS, NETWORK, PASSIVE 3가지
        result += LocationManager.GPS_PROVIDER + " : " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) + "\n";
        result += LocationManager.NETWORK_PROVIDER + " : " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) + "\n";
        result += LocationManager.PASSIVE_PROVIDER + " : " + locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) + "\n";



        //최적의 제공자 선택

        /* criteria 속성 (setAccuracy 변수로 들어갈수 있는것)
        NO_REQUIREMENT : 상관없다.
        ACCURACY_COARSE : 대충의 정밀도를 요구
        ACCURACY_FIME : 정밀한 정밀도를 요구
        POWER_HIGH : 배터리를 많이 사용해도됌
        POWER_MEDIUM : 배터리를 많게도 적게도 아닌 중간정도로 사용해도됌
        POWER_LOW : 배터리를 조금 사용해야함

        => 최적의 상태를 검사할때 위와같은 변수들을 입력하면 그것들을 기준으로 판단한다.
         */

        Criteria crit = new Criteria();
        crit.setAccuracy(Criteria.NO_REQUIREMENT); //정확성을 어떻게줄거냐
        crit.setPowerRequirement(Criteria.NO_REQUIREMENT); //배터리파워
        crit.setAltitudeRequired(false);  //추가비용(시간,범위)에 대한것1
        crit.setCostAllowed(false);  //추가비용(시간,범위)에 대한것 2
        String best = locationManager.getBestProvider(crit, true);
        result += ("\n best provider : " + best + "\n\n");

        //결과출력
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(result);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(locationTag){
                    LatLng loadPoint = new LatLng(location.getLatitude(), location.getLongitude());
                    gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(loadPoint,15));

                    locationTag = false;
                }

                //위치정보바뀔때
                tv_location.setText("위도 : " + location.getLatitude() + "경도 : " + location.getLongitude());

                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.KOREA);
                double latValue = location.getLatitude();
                double lngValue = location.getLongitude();

                try{
                    //address 객체 존재
                    List<Address> addressList = geocoder.getFromLocation(latValue, lngValue, 4); //4는 최댓값, 몇개를 가져오는가
                    StringBuilder builder = new StringBuilder();

                    for(Address ad : addressList){
                        builder.append(ad.getAddressLine(0)).append("\n") //실제주소(도로명주소)
                                .append(ad.getPostalCode()).append("\n") //우편번호주소 (우리나라는 아직안나오고있다)
                                .append(ad.getLocality()).append("\n") //"구역" 주소 ex)서대문구
                                .append(ad.getCountryName()).append("\n\n"); // 국가명
                    }

                    tv_address.setText(builder.toString());
                }catch(Exception e){

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //와이파이같은거 끄고켰을때 provider가 바뀌때
            }

            @Override
            public void onProviderEnabled(String provider) {
                //provider들이 사용가능할때
            }

            @Override
            public void onProviderDisabled(String provider) {
                //provider들이 사용불가능할떄
            }
        };

        getLocation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    //일단 다불러서 값을 저장한다. 그리고 나중에 하나를 사용한다.
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000, 0, locationListener);
                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                }catch(SecurityException e){
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    
                }
            }
        }); //OnClick End
    }

}
