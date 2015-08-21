package dnr.capitalone.com.dealandreward;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Blob;

/**
 * Created by ZGP046 on 7/21/2015.
 */
public class CouponDetails implements Parcelable{

private String couponId;
private String merchant;
private String couponType;
private String address;
private String zipcode;
private String lat;
private String lng;
private String couponInfo;
private String image;
    private String facevalue;
    private String discount;

    public String getFacevalue() {
        return facevalue;
    }

    public void setFacevalue(String facevalue) {
        this.facevalue = facevalue;
    }

    public String getCouponId() {
            return couponId;
        }

        public void setCouponId(String couponId) {
            this.couponId = couponId;
        }

        public String getMerchant() {
            return merchant;
        }

        public void setMerchant(String merchant) {
            this.merchant = merchant;
        }

        public String getCouponType() {
            return couponType;
        }

        public void setCouponType(String couponType) {
            this.couponType = couponType;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }



    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getCouponInfo() {
            return couponInfo;
        }

        public void setCouponInfo(String couponInfo) {
            this.couponInfo = couponInfo;
        }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
