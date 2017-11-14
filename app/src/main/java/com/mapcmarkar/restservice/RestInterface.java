package com.mapcmarkar.restservice;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RestInterface {

  //  String BASE_URL = "http://myphpdevelopers.com/dev/seekingdaddie/";
    String BASE_URL = "http://floridaconstruct.eu/";


    @GET("comenzi/test/testmap.php")
    Call<ResponseBody> testmap();
    @FormUrlEncoded
    @POST("wp-admin/admin-post.php")
    Call<ResponseBody> setProfilePicFromAlbum(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("wp-admin/admin-post.php")
    Call<ResponseBody> deleteProfilePicByUid(@FieldMap Map<String, String> params);
    /*@Multipart
    @POST("api/doctors/createPrescription")
    Call<ResponseBody> createPrescription(
            @Part("type") RequestBody type,
            @Part("reference_number") RequestBody reference_number,
            @Part("user_family_id") RequestBody user_family_id,
            @Part("upload_by") RequestBody upload_by,
            @Part("purpose") RequestBody purpose,
            @Part("is_virtual") RequestBody is_virtual,
            @Part("can_delete") RequestBody can_delete,
            @Part("chief_complaints") RequestBody chief_complaints,
            @Part("examination_and_findings") RequestBody examination_and_findings,
            @Part("medicine_list") RequestBody medicine_list,
            @Part("has_signature") RequestBody has_signature,
            @Part List<MultipartBody.Part> files);*/

    @Multipart
    @POST("wp-admin/admin-post.php")
    Call<ResponseBody> postProfImage(@Part MultipartBody.Part image, @Part("user_id") RequestBody user_id, @Part("action") RequestBody action);
    @FormUrlEncoded
    @POST("wp-admin/admin-post.php")
    Call<ResponseBody> isUserNameEmailExist(@FieldMap Map<String, String> params);

}
