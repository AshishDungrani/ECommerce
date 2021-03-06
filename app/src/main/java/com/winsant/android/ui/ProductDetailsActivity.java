package com.winsant.android.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.winsant.android.R;
import com.winsant.android.actionitembadge.library.ActionItemBadge;
import com.winsant.android.adapter.AttributeListAdapter;
import com.winsant.android.adapter.ColorAttributeListAdapter;
import com.winsant.android.adapter.GeneralFeatureAdapter;
import com.winsant.android.adapter.ImageSliderAdapter;
import com.winsant.android.adapter.ProductOffersListAdapter;
import com.winsant.android.adapter.SizeAttributeListAdapter;
import com.winsant.android.kprogresshud.KProgressHUD;
import com.winsant.android.like.LikeButton;
import com.winsant.android.like.OnLikeListener;
import com.winsant.android.model.AttributeModel;
import com.winsant.android.model.GeneralFeaturesModel;
import com.winsant.android.model.OfferModel;
import com.winsant.android.model.SizeModel;
import com.winsant.android.utils.CommonDataUtility;
import com.winsant.android.utils.DividerItemDecoration;
import com.winsant.android.utils.StaticDataUtility;
import com.winsant.android.views.CirclePageIndicator;
import com.winsant.android.views.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Pc-Android-1 on 10/14/2016.
 * <p>
 * TODO : Specific Product Details Display Activity
 */

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity activity;
    private TextView mToolbar_title;

    private ProgressWheel progress_wheel;
    private ScrollView product_view;
//    private RelativeLayout rl_no_data;
//    private TextView emptyData;

    private ImageView imgError;
    private ArrayList<String> productImages;
    private ArrayList<GeneralFeaturesModel> generalFeaturesModels;
    private ArrayList<AttributeModel> attributeModelArrayList;
    private ArrayList<SizeModel> sizeModelArrayList, finalSizeModelArrayList;
    private ArrayList<String> colorImagesList;
    private ArrayList<OfferModel> offersArrayList;

    private ViewPager viewPager;
    private CirclePageIndicator indicator;

    private RelativeLayout rl_main;
    private RecyclerView generalFeatureList, offerList;
    private CardView cardGeneralFeature, cardColorSize, cardOffers;
    private LinearLayout tblAttribute;
    private LinearLayout llSingleAttribute;

    private TextView txtName;
    private TextView txtDiscountPrice;
    private TextView txtPrice;
    private TextView txtDiscount;
    private TextView txtPinCode;
    private TextView txtDetails;
    private TextView txtShippingCharge;
    private TextView item_sold_out;
    private TextView txtSingleAttribute;

    private boolean is_item_sold_out = false;

    //    private ImageView imgWishList;
    private LikeButton heart_button;

    private Button btnBuyNow, btnAddCart;
    private String url, strAttribute = "", fav_link, remove_link, buy_now_link, cart_url, is_wishlist, is_attribute, strPinCode = "";
    private String TYPE = "";

    private KProgressHUD progressHUD;
    private MenuItem cart;

    private RecyclerView AttributeList;
    private RecyclerView SizeList;
    private RecyclerView ColorList;
    private TextView txtColor, txtSize;
    private String ColorName = "", SizeName = "", ColorId = "", SizeId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        activity = ProductDetailsActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            mToolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        }
        mToolbar_title.setTypeface(CommonDataUtility.setTitleTypeFace(activity));

        if (getIntent().hasExtra("name"))
            mToolbar_title.setText(getIntent().getStringExtra("name"));
        else
            mToolbar_title.setText(getString(R.string.app_name));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ico_arrow_back_svg);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        url = getIntent().getStringExtra("url");

        setUI();
    }

    private void setUI() {

        rl_main = (RelativeLayout) findViewById(R.id.rl_main);

        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        product_view = (ScrollView) findViewById(R.id.product_view);
        imgError = (ImageView) findViewById(R.id.imgError);

        llSingleAttribute = (LinearLayout) findViewById(R.id.llSingleAttribute);

        cardGeneralFeature = (CardView) findViewById(R.id.cardGeneralFeature);
        cardColorSize = (CardView) findViewById(R.id.cardColorSize);
        cardOffers = (CardView) findViewById(R.id.cardOffers);

        generalFeatureList = (RecyclerView) findViewById(R.id.generalFeatureList);
        generalFeatureList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        offerList = (RecyclerView) findViewById(R.id.offerList);
        offerList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        // TODO : Attribute
        AttributeList = (RecyclerView) findViewById(R.id.AttributeList);
        AttributeList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        ColorList = (RecyclerView) findViewById(R.id.ColorList);
        SizeList = (RecyclerView) findViewById(R.id.SizeList);
        ColorList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        SizeList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        tblAttribute = (LinearLayout) findViewById(R.id.tblAttribute);
        // TODO : Attribute END

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);

        txtName = (TextView) findViewById(R.id.txtName);
        txtDiscountPrice = (TextView) findViewById(R.id.txtDiscountPrice);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtDiscount = (TextView) findViewById(R.id.txtDiscount);
        heart_button = (LikeButton) findViewById(R.id.heart_button);
//        imgWishList = (ImageView) findViewById(R.id.imgWishList);
        ImageView imgShare = (ImageView) findViewById(R.id.imgShare);
        txtPinCode = (TextView) findViewById(R.id.txtPinCode);
        txtDetails = (TextView) findViewById(R.id.txtDetails);
        txtShippingCharge = (TextView) findViewById(R.id.txtShippingCharge);
        txtColor = (TextView) findViewById(R.id.txtColor);
        txtSize = (TextView) findViewById(R.id.txtSize);
        txtSingleAttribute = (TextView) findViewById(R.id.txtSingleAttribute);
        item_sold_out = (TextView) findViewById(R.id.item_sold_out);

        txtName.setTypeface(CommonDataUtility.setTypeFace1(activity));
        txtDiscountPrice.setTypeface(CommonDataUtility.setTypeFace(activity));
        txtPrice.setTypeface(CommonDataUtility.setTitleTypeFace(activity));
        txtDiscount.setTypeface(CommonDataUtility.setTypeFace1(activity));
        txtPinCode.setTypeface(CommonDataUtility.setTypeFace1(activity));
        txtDetails.setTypeface(CommonDataUtility.setTypeFace1(activity));
        txtShippingCharge.setTypeface(CommonDataUtility.setTypeFace1(activity));
        txtColor.setTypeface(CommonDataUtility.setTypeFace1(activity));
        txtSize.setTypeface(CommonDataUtility.setTypeFace1(activity));
        txtSingleAttribute.setTypeface(CommonDataUtility.setTypeFace1(activity));
        item_sold_out.setTypeface(CommonDataUtility.setTypeFace1(activity));

        Button btnChange = (Button) findViewById(R.id.btnChange);
        btnBuyNow = (Button) findViewById(R.id.btnBuyNow);
        btnAddCart = (Button) findViewById(R.id.btnAddCart);

        btnChange.setTypeface(CommonDataUtility.setTypeFace(activity));
        btnBuyNow.setTypeface(CommonDataUtility.setTypeFace(activity));
        btnAddCart.setTypeface(CommonDataUtility.setTypeFace(activity));

        if (CommonDataUtility.isTablet(activity)) {

            txtName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            txtDiscountPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            txtPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            txtDiscount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            txtDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            txtShippingCharge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            txtColor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            txtSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            txtSingleAttribute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            item_sold_out.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            btnBuyNow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            btnAddCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            btnChange.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        } else {

            txtName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            txtDiscountPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            txtPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            txtDiscount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            txtDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            txtShippingCharge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            txtColor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            txtSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            txtSingleAttribute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            item_sold_out.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            btnBuyNow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            btnAddCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            btnChange.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }

        imgError.setOnClickListener(this);
        imgShare.setOnClickListener(this);
//        heart_button.setOnClickListener(this);
        imgShare.setOnClickListener(this);
        btnChange.setOnClickListener(this);
        btnBuyNow.setOnClickListener(this);
        btnAddCart.setOnClickListener(this);
//        txtColor.setOnClickListener(this);
//        txtSize.setOnClickListener(this);
        txtPinCode.setOnClickListener(this);

        if (!MyApplication.getInstance().getPreferenceUtility().getString("pincode").equals("")) {
            strPinCode = MyApplication.getInstance().getPreferenceUtility().getString("pincode");
            txtPinCode.setText((Html.fromHtml("Deliver to " + "<font color='#0787EA'> <b>" + strPinCode + "</b></font>")));
        } else {
            strPinCode = "";
            txtPinCode.setText((Html.fromHtml("Deliver to : " + "<font color='#0787EA'>____________</font>")));
        }

        heart_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                setWishListIcon();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                setWishListIcon();
            }
        });

        getData();
    }

    private void setWishListIcon() {
        if (CommonDataUtility.checkConnection(activity)) {
            if (MyApplication.getInstance().getPreferenceUtility().getLogin()) {
                if (is_wishlist.equals("1")) {
                    addRemoveWishList(remove_link, "remove");
                } else {
                    addRemoveWishList(fav_link, "add");
                }
            } else {
                Toast.makeText(activity, "Please login first to add product in wishlist", Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        } else {
            CommonDataUtility.showSnackBar(rl_main, getString(R.string.no_internet));
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {
            case R.id.emptyData:

                if (CommonDataUtility.checkConnection(activity)) {
                    getData();
                } else if (TYPE.equals(getResources().getString(R.string.no_data))
                        || TYPE.equals(getResources().getString(R.string.no_connection))) {
                    getData();
                } else if (TYPE.equals(getResources().getString(R.string.no_internet))) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
                break;

            case R.id.imgShare:
                shareApp();
                break;

            case R.id.imgWishList:

                break;

            case R.id.txtPinCode:
            case R.id.btnChange:
                changePinDialog();
                break;

            case R.id.btnBuyNow:

                setAddToCart_BuyNow(buy_now_link, "buynow");
                break;

            case R.id.btnAddCart:

                setAddToCart_BuyNow(cart_url, "addtocart");
                break;

//            case R.id.txtColor:
//            case R.id.txtSize:
//
//                if (strAttribute.equals("both")) {
//
//                    tblAttribute.setVisibility(View.VISIBLE);
//                    llSingleAttribute.setVisibility(View.GONE);
//
//                    intent = new Intent(activity, AttributeActivity.class);
//                    intent.putExtra("name", txtName.getText().toString());
//                    intent.putExtra("attrib", attributeModelArrayList);
//                    startActivityForResult(intent, 2);// Activity is started with requestCode 2
//                    overridePendingTransition(R.anim.slide_up, 0);
//
//                }
//
//                break;
            default:
                break;
        }
    }

    public void shareApp() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Winsant");
        intent.putExtra(Intent.EXTRA_TEXT, url.replace("api.winsant.com", "www.winsant.com"));
        activity.startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void setAddToCart_BuyNow(String url, String tag) {

        if (CommonDataUtility.checkConnection(activity)) {
            if (strPinCode.equals("")) {
                CommonDataUtility.showSnackBar(rl_main, "Enter pin code first");
                changePinDialog();
            } else {

                if (MyApplication.getInstance().getPreferenceUtility().getLogin()) {
                    if (is_attribute.equals("1") && strAttribute.equals("both")) {
                        if (ColorId.equals("")) {
                            CommonDataUtility.showSnackBar(rl_main, "Please select COLOR");
                        } else if (SizeId.equals("")) {
                            CommonDataUtility.showSnackBar(rl_main, "Please select SIZE");
                        } else {
                            AddToCart(url, tag);
                        }
                    } else if (is_attribute.equals("1") && strAttribute.equals("color")) {
                        if (ColorId.equals("")) {
                            CommonDataUtility.showSnackBar(rl_main, "Please select COLOR");
                        } else {
                            AddToCart(url, tag);
                        }
                    } else if (is_attribute.equals("1") && strAttribute.equals("size")) {
                        if (SizeId.equals("")) {
                            CommonDataUtility.showSnackBar(rl_main, "Please select SIZE");
                        } else {
                            AddToCart(url, tag);
                        }
                    } else {
                        AddToCart(url, tag);
                    }
                } else {
                    Toast.makeText(activity, "Please login first to add product in cart", Toast.LENGTH_SHORT).show();
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }
            }
        } else {
            CommonDataUtility.showSnackBar(rl_main, getString(R.string.no_internet));
        }
    }

    private void getData() {

        product_view.setVisibility(View.GONE);

        if (CommonDataUtility.checkConnection(activity)) {

            imgError.setVisibility(View.GONE);
            getProductData();

        } else {

            imgError.setVisibility(View.VISIBLE);
            TYPE = getString(R.string.no_internet);
            Glide.with(activity).load(R.drawable.no_wifi).into(imgError);
        }
    }

    private void getProductData() {

        progress_wheel.setVisibility(View.VISIBLE);
        productImages = new ArrayList<>();
        generalFeaturesModels = new ArrayList<>();
        attributeModelArrayList = new ArrayList<>();
        offersArrayList = new ArrayList<>();

        JSONObject obj = new JSONObject();
        try {

            obj.put("userid", MyApplication.getInstance().getPreferenceUtility().getUserId());
            System.out.println(StaticDataUtility.APP_TAG + " product details param --> " + obj.toString());

        } catch (Exception e) {
            System.out.println(StaticDataUtility.APP_TAG + " product details param error --> " + e.toString());
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            System.out.println(StaticDataUtility.APP_TAG + " product details response --> " + response.toString());

                            JSONObject jsonObject = new JSONObject(response.toString());
                            final String message = jsonObject.optString("msg");
                            final String success = jsonObject.optString("success");

                            if (success.equals("1")) {

                                JSONObject data = jsonObject.optJSONObject("data");

                                JSONArray images = data.optJSONArray("images");
                                for (int i = 0; i < images.length(); i++) {
                                    productImages.add(images.optJSONObject(i).optString("image_url"));
                                }

                                JSONArray general_features = data.optJSONArray("general_features");
                                if (general_features.length() > 0) {
                                    for (int i = 0; i < general_features.length(); i++) {

                                        JSONObject general_featuresObject = general_features.getJSONObject(i);

                                        generalFeaturesModels.add(new GeneralFeaturesModel(general_featuresObject.optString("attr_title"),
                                                general_featuresObject.optString("attr_value")));
                                    }
                                }

                                is_attribute = data.optString("is_attribute");
                                if (is_attribute.equals("1")) {

                                    cardColorSize.setVisibility(View.VISIBLE);

                                    JSONArray attribute = data.optJSONArray("attribute");

                                    HashSet<SizeModel> hs = new HashSet<SizeModel>();
                                    HashSet<String> hs1 = new HashSet<String>();

                                    if (attribute.length() > 0) {
                                        for (int i = 0; i < attribute.length(); i++) {

                                            JSONObject attributeObject = attribute.getJSONObject(i);

                                            JSONArray size = attributeObject.optJSONArray("size");
                                            sizeModelArrayList = new ArrayList<>();
                                            colorImagesList = new ArrayList<>();
                                            finalSizeModelArrayList = new ArrayList<>();

                                            for (int j = 0; j < size.length(); j++) {

                                                JSONObject sizeObject = size.getJSONObject(j);

                                                sizeModelArrayList.add(new SizeModel(sizeObject.optString("size_id"), sizeObject.optString("size_name"), "0",
                                                        sizeObject.optString("price"), sizeObject.optString("discount_price"), sizeObject.optString("discount_per")));

                                                hs1.add(sizeObject.optString("size_name"));
                                                hs.addAll(sizeModelArrayList);
                                            }

                                            JSONArray color_images = attributeObject.optJSONArray("color_images");
                                            for (int j = 0; j < color_images.length(); j++) {
                                                colorImagesList.add(color_images.getJSONObject(j).optString("image_url"));
                                            }

                                            if (attributeObject.optString("color_id").equals("")) {
                                                strAttribute = "size";
                                                finalSizeModelArrayList.addAll(hs);
                                            } else if (sizeModelArrayList.size() > 0) {
                                                strAttribute = "both";
                                                attributeModelArrayList.add(new AttributeModel(attributeObject.optString("color_id"),
                                                        attributeObject.optString("color_name"), sizeModelArrayList, "0",
                                                        attributeObject.optString("color_image"), colorImagesList));
                                            } else {
                                                strAttribute = "color";
                                                attributeModelArrayList.add(new AttributeModel(attributeObject.optString("color_id"),
                                                        attributeObject.optString("color_name"), "0", attributeObject.optString("color_image"), colorImagesList));
                                            }
                                        }

                                        txtColor.setText("Color (" + attributeModelArrayList.size() + ")");
                                        txtSize.setText("Size (" + hs1.size() + ")");
                                    }
                                } else {
                                    cardColorSize.setVisibility(View.GONE);
                                }

                                if (jsonObject.optJSONArray("coupon_data").length() > 0) {
                                    cardOffers.setVisibility(View.VISIBLE);
                                    JSONArray coupon_data = jsonObject.optJSONArray("coupon_data");

                                    for (int i = 0; i < coupon_data.length(); i++)
                                        offersArrayList.add(new OfferModel(coupon_data.optJSONObject(i).optString("coupon_title"),
                                                coupon_data.optJSONObject(i).optString("coupon_description"), coupon_data.optJSONObject(i).optString("t_and_c")));

                                    setOfferData();
                                } else {
                                    cardOffers.setVisibility(View.GONE);
                                }

                                // TODO : Set Data
//                                productId = data.optString("product_id");
                                txtName.setText(data.optString("product_name"));

                                if (data.optString("discount_per").equals("0")) {
                                    txtPrice.setVisibility(View.GONE);
                                    txtDiscount.setVisibility(View.GONE);
                                    txtDiscountPrice.setText(activity.getResources().getString(R.string.Rs) + " " + data.optString("price"));
                                } else {
                                    txtPrice.setText(activity.getResources().getString(R.string.Rs) + " " + data.optString("price"));
                                    txtPrice.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    txtDiscountPrice.setText(activity.getResources().getString(R.string.Rs) + " " + data.optString("discount_price"));
                                    txtDiscount.setText(String.format("%s %% OFF", data.optString("discount_per")));
                                }

                                txtDetails.setText(Html.fromHtml(data.optString("description")));

                                if (data.optString("shipping_amount").equals("0"))
                                    txtShippingCharge.setText("Free Shipping");
                                else
                                    txtShippingCharge.setText("Shipping Charge " + activity.getResources().getString(R.string.Rs)
                                            + " " + data.optString("shipping_amount"));

                                fav_link = data.optString("fav_link");
                                remove_link = data.optString("remove_link");
                                cart_url = data.optString("cart_url");
                                buy_now_link = data.optString("buy_now_link");

                                if (data.optString("is_wishlist").equals("1")) {
                                    is_wishlist = "1";
                                    heart_button.setLiked(true);
//                                    imgWishList.setImageResource(R.drawable.ico_wishlist_selected_svg);
                                } else {
                                    is_wishlist = "0";
                                    heart_button.setLiked(false);
//                                    imgWishList.setImageResource(R.drawable.ico_wishlist_normal_svg);
                                }

                                setProductImages();

                                if (data.optString("availability").equals("0")) {
                                    btnBuyNow.setVisibility(View.GONE);
                                    btnAddCart.setVisibility(View.GONE);
                                    item_sold_out.setVisibility(View.VISIBLE);
                                    is_item_sold_out = true;
                                } else {
                                    btnBuyNow.setVisibility(View.VISIBLE);
                                    btnAddCart.setVisibility(View.VISIBLE);
                                    item_sold_out.setVisibility(View.GONE);
                                    is_item_sold_out = false;
                                }

                                if (generalFeaturesModels.size() > 0) {
                                    cardGeneralFeature.setVisibility(View.VISIBLE);
                                    generalFeatureList.setAdapter(new GeneralFeatureAdapter(activity, generalFeaturesModels));
                                    generalFeatureList.addItemDecoration(new DividerItemDecoration(activity, R.drawable.divider));
                                } else {
                                    cardGeneralFeature.setVisibility(View.GONE);
                                }

                                setAttributeData();

                                progress_wheel.setVisibility(View.GONE);
                                product_view.setVisibility(View.VISIBLE);

                            } else {

                                noDataError();
                                buttonEnable(false);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                            noDataError();
                            buttonEnable(false);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progress_wheel.setVisibility(View.GONE);
                product_view.setVisibility(View.GONE);

                buttonEnable(false);

                imgError.setVisibility(View.VISIBLE);

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    TYPE = getString(R.string.no_internet);
                    Glide.with(activity).load(R.drawable.no_wifi).into(imgError);
                } else {
                    noServerError();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        Volley.newRequestQueue(activity).add(jsonObjReq);
    }

    private void setProductImages() {
        viewPager.setAdapter(new ImageSliderAdapter(activity, productImages));
        indicator.setViewPager(viewPager);
    }

    private void setAttributeData() {

        switch (strAttribute) {
            case "both":

                tblAttribute.setVisibility(View.VISIBLE);
                llSingleAttribute.setVisibility(View.GONE);

                AttributeListAdapter attributeListAdapter = new AttributeListAdapter(activity, attributeModelArrayList, SizeList,
                        new AttributeListAdapter.onClickListener() {
                            @Override
                            public void onColorClick(ArrayList<String> images, String color_id, String color_name) {

//                                showProgress();

                                if (images.size() > 0) {

                                    productImages.clear();
                                    productImages.addAll(images);
                                    setProductImages();
                                }

                                ColorId = color_id;
                                ColorName = color_name;
                                txtColor.setText(Html.fromHtml("Color : <font color='#0787EA'><b>" + ColorName + "</b></font>"));

//                                dismissProgress();
                            }

                            @Override
                            public void onSizeClick(String size_id, String size_name, String price, String discount_price, String discount_per) {
                                if (ColorId.equals("") || ColorName.equals("")) {
                                    CommonDataUtility.showSnackBar(rl_main, "Please select COLOR first!");
                                } else {

//                                    showProgress();

                                    SizeId = size_id;
                                    SizeName = size_name;
                                    txtSize.setText(Html.fromHtml("Size : <font color='#0787EA'><b>" + SizeName + "</b></font>"));

                                    if (discount_per.equals("0")) {
                                        txtPrice.setVisibility(View.GONE);
                                        txtDiscount.setVisibility(View.GONE);
                                        txtDiscountPrice.setText(activity.getResources().getString(R.string.Rs) + " " + price);
                                    } else {
                                        txtPrice.setText(activity.getResources().getString(R.string.Rs) + " " + price);
                                        txtPrice.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                        txtDiscountPrice.setText(activity.getResources().getString(R.string.Rs) + " " + discount_price);
                                        txtDiscount.setText(String.format("%s %% OFF", discount_per));
                                    }

//                                    dismissProgress();
                                }
                            }

                            @Override
                            public void onSizeClick(String size_id, String size_name) {
                                SizeId = size_id;
                                SizeName = size_name;
                                txtSize.setText(Html.fromHtml("Size :- <font color='#0787EA'><b>" + SizeName + "</b></font>"));
                            }
                        });

                ColorList.setAdapter(attributeListAdapter);

                break;
            case "size":

                tblAttribute.setVisibility(View.GONE);
                llSingleAttribute.setVisibility(View.VISIBLE);

                txtSingleAttribute.setText("Size : ");
                SizeAttributeListAdapter sizeAttributeListAdapter = new SizeAttributeListAdapter(activity, finalSizeModelArrayList,
                        new SizeAttributeListAdapter.onClickListener() {
                            @Override
                            public void onClick(String size_id, String size_name, String price, String discount_price, String discount_per) {
                                txtSingleAttribute.setText(Html.fromHtml("Size : <font color='#0787EA'><b>" + size_name + "</b></font>"));
                                SizeId = size_id;
                            }
                        });

                AttributeList.setAdapter(sizeAttributeListAdapter);

                break;

            case "color":

                tblAttribute.setVisibility(View.GONE);
                llSingleAttribute.setVisibility(View.VISIBLE);

                txtSingleAttribute.setText("Color : ");
                ColorAttributeListAdapter colorAttributeListAdapter = new ColorAttributeListAdapter(activity, attributeModelArrayList,
                        new ColorAttributeListAdapter.onClickListener() {
                            @Override
                            public void onClick(String color_id, String color_name) {
                                txtSingleAttribute.setText(Html.fromHtml("Color : <font color='#0787EA'><b>" + color_name + "</b></font>"));
                                ColorId = color_id;
                            }
                        });

                AttributeList.setAdapter(colorAttributeListAdapter);

                break;

            default:
                break;
        }
    }

    private void setOfferData() {

        ProductOffersListAdapter productOffersListAdapter = new ProductOffersListAdapter(activity, offersArrayList,
                new ProductOffersListAdapter.onClickListener() {
                    @Override
                    public void onClick(String t_and_c) {
                        TermsAndConditionDialog(t_and_c);
                    }
                });

        offerList.setAdapter(productOffersListAdapter);
    }

    private void TermsAndConditionDialog(String t_and_c) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_change_pincode, null);
        dialog.setContentView(dialogView);

        dialogView.findViewById(R.id.txtAvailability).setVisibility(View.GONE);

        TextView txtTitle = (TextView) dialogView.findViewById(R.id.txtTitle);
        txtTitle.setText("Terms & Condition");

        TextView txtTerms = (TextView) dialogView.findViewById(R.id.txtTerms);
        txtTerms.setVisibility(View.VISIBLE);
        txtTerms.setText(t_and_c);

        if (CommonDataUtility.isTablet(activity)) {
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            txtTerms.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        } else {
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            txtTerms.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }

        dialogView.findViewById(R.id.edtPinCode).setVisibility(View.GONE);
        dialogView.findViewById(R.id.view1).setVisibility(View.GONE);

        Button btnOK = (Button) dialogView.findViewById(R.id.btnOK);
        btnOK.setTypeface(CommonDataUtility.setTypeFace(activity));
        dialogView.findViewById(R.id.btnCancel).setVisibility(View.GONE);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void buttonEnable(boolean b) {
        btnBuyNow.setEnabled(b);
        btnAddCart.setEnabled(b);
    }

    private void noDataError() {
        progress_wheel.setVisibility(View.GONE);
        imgError.setVisibility(View.VISIBLE);
        TYPE = getString(R.string.no_data);
        Glide.with(activity).load(R.drawable.no_data).into(imgError);
    }

    private void noServerError() {
        progress_wheel.setVisibility(View.GONE);
        TYPE = getString(R.string.no_connection);
        Glide.with(activity).load(R.drawable.no_server).into(imgError);
    }

    private void addRemoveWishList(String fav_link, final String favType) {

        JSONObject obj = new JSONObject();
        try {

            obj.put("userid", MyApplication.getInstance().getPreferenceUtility().getUserId());
            System.out.println(StaticDataUtility.APP_TAG + " addRemoveWishList param --> " + obj.toString());

        } catch (Exception e) {
            System.out.println(StaticDataUtility.APP_TAG + " addRemoveWishList param error --> " + e.toString());
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, fav_link, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(StaticDataUtility.APP_TAG + " addRemoveWishList response --> " + response);

                        try {

                            JSONObject jsonObject = new JSONObject(response.toString());
                            final String message = jsonObject.optString("message");
                            final String success = jsonObject.optString("success");

                            if (success.equals("1")) {

                                dismissProgress();

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (favType.equals("add")) {
                                            is_wishlist = "1";
//                                            imgWishList.setImageResource(R.drawable.ico_wishlist_selected_svg);
//
//                                            new ParticleSystem(activity, 10, R.drawable.animated_confetti, 1500)
//                                                    .setSpeedRange(0.1f, 0.25f)
//                                                    .setRotationSpeedRange(90, 180)
//                                                    .setInitialRotationRange(0, 180)
//                                                    .oneShot(imgWishList, 50);

                                        } else {
                                            is_wishlist = "0";
//                                            imgWishList.setImageResource(R.drawable.ico_wishlist_normal_svg);
                                        }

                                        CommonDataUtility.showSnackBar(rl_main, message);
                                    }
                                });

                            } else {
                                showError(favType);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showError(favType);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showError(favType);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        Volley.newRequestQueue(activity).add(jsonObjReq);
    }

    private void showProgress() {
        progressHUD = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false).show();
    }

    private void dismissProgress() {
        if (progressHUD != null && progressHUD.isShowing())
            progressHUD.dismiss();
    }

    private void showError(String favType) {
        if (favType.equals("add")) {
            Toast.makeText(activity, "Problem while adding from the wish list. Try again later", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Problem while removing from the wishlist. Try again later", Toast.LENGTH_SHORT).show();
        }

        dismissProgress();
    }

    private void changePinDialog() {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_change_pincode, null);
        dialog.setContentView(dialogView);

        TextView txtAvailability = (TextView) dialogView.findViewById(R.id.txtAvailability);
        TextView txtTitle = (TextView) dialogView.findViewById(R.id.txtTitle);
        txtTitle.setText("Enter Delivery Pin Code");

        txtAvailability.setVisibility(View.GONE);

        final EditText edtPinCode = (EditText) dialogView.findViewById(R.id.edtPinCode);
        edtPinCode.setTypeface(CommonDataUtility.setTypeFace1(activity));
        edtPinCode.setHint("Enter pin code here...");

        if (activity.getResources().getBoolean(R.bool.isLargeTablet)) {
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            edtPinCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        } else if (activity.getResources().getBoolean(R.bool.isTablet)) {
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            edtPinCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        } else {
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            edtPinCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }

        if (!MyApplication.getInstance().getPreferenceUtility().getString("pincode").equals(""))
            edtPinCode.setText(MyApplication.getInstance().getPreferenceUtility().getString("pincode"));

        Button btnOK = (Button) dialogView.findViewById(R.id.btnOK);
        btnOK.setTypeface(CommonDataUtility.setTypeFace(activity));
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnCancel.setTypeface(CommonDataUtility.setTypeFace(activity));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtPinCode.getText().toString().equals("")) {
                    edtPinCode.setError("Enter pin code");
                } else {
                    PinCodeVerify(edtPinCode.getText().toString());
                }

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.activity_action_cart)).setIcon(R.drawable.ico_menu_cart).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        cart = menu.findItem(1);
        setBadge();
        return true;
    }

    private void setBadge() {

        if (MyApplication.getInstance().getPreferenceUtility().getLogin()) {
            int total = MyApplication.getInstance().getPreferenceUtility().getInt("total_cart");
            if (!(total == 0))
                ActionItemBadge.Update(this, cart, R.drawable.ico_menu_cart, StaticDataUtility.style, total);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case 1:
                startActivity(new Intent(activity, CartActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onResume() {
        super.onResume();
        setBadge();
        invalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Glide.clear(imgError);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Glide.clear(imgError);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // check if the request code is same as what is passed  here it is 2
//        if (requestCode == 2) {
//            String getData = data.getStringExtra("type");
//
//            if (getData.equals("yes")) {
//                txtColor.setText(Html.fromHtml("Color :- <font color='#0787EA'><b>" + data.getStringExtra("color") + "</b></font>"));
//                txtSize.setText(Html.fromHtml("Size :- <font color='#0787EA'><b>" + data.getStringExtra("size") + "</b></font>"));
//
//
//                ColorId = data.getStringExtra("ColorId");
//                SizeId = data.getStringExtra("SizeId");
//            }
//        }
//    }

    private void AddToCart(String cart_url, final String type) {

        showProgress();

        JSONObject obj = new JSONObject();
        try {

            obj.put("userid", MyApplication.getInstance().getPreferenceUtility().getUserId());
            obj.put("product_color", ColorName);
            obj.put("color_id", ColorId);
            obj.put("product_size", SizeName);
            obj.put("size_id", SizeId);
            obj.put("qty", "1");

            System.out.println(StaticDataUtility.APP_TAG + " AddToCart param --> " + obj.toString());

        } catch (Exception e) {
            System.out.println(StaticDataUtility.APP_TAG + " AddToCart error --> " + e.toString());
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, cart_url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(StaticDataUtility.APP_TAG + " AddToCart response --> " + response);

                        try {

                            JSONObject jsonObject = new JSONObject(response.toString());
                            final String message = jsonObject.optString("message");
                            final String success = jsonObject.optString("success");

                            if (success.equals("1")) {

                                dismissProgress();

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        int total = MyApplication.getInstance().getPreferenceUtility().getInt("total_cart");
                                        MyApplication.getInstance().getPreferenceUtility().setInt("total_cart", (total + 1));

                                        setBadge();

                                        if (type.equals("addtocart")) {
//                                            setAttributeData();
                                            CommonDataUtility.showSnackBar(rl_main, message);
                                        } else {
                                            startActivity(new Intent(activity, CartActivity.class));
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        }
                                    }
                                });

                            } else {
                                showErrorMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorMessage("Problem while adding products into the cart. Try again later");
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorMessage("Problem while adding product into the cart. Try again later");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        Volley.newRequestQueue(activity).add(jsonObjReq);
    }

    private void PinCodeVerify(final String zip_code) {

        showProgress();

        JSONObject obj = new JSONObject();
        try {

            obj.put("zip_code", zip_code);

            System.out.println(StaticDataUtility.APP_TAG + " PinCodeVerify param --> " + obj.toString());

        } catch (Exception e) {
            System.out.println(StaticDataUtility.APP_TAG + " PinCodeVerify error --> " + e.toString());
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, StaticDataUtility.SERVER_URL + StaticDataUtility.ZIP_CODE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(StaticDataUtility.APP_TAG + " PinCodeVerify response --> " + response);

                        try {

                            JSONObject jsonObject = new JSONObject(response.toString());
                            final String message = jsonObject.optString("message");
                            final String success = jsonObject.optString("success");
                            final String cod = jsonObject.optString("cod");
                            final String delivery = jsonObject.optString("delivery");

                            if (success.equals("1")) {

                                dismissProgress();

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonDataUtility.showSnackBar(rl_main, message);

                                        MyApplication.getInstance().getPreferenceUtility().setString("pincode", zip_code);
                                        strPinCode = MyApplication.getInstance().getPreferenceUtility().getString("pincode");

                                        if (is_item_sold_out) {
                                            btnBuyNow.setVisibility(View.GONE);
                                            btnAddCart.setVisibility(View.GONE);
                                            item_sold_out.setVisibility(View.VISIBLE);

                                        } else {

                                            if (delivery.equals("0")) {
                                                btnBuyNow.setVisibility(View.GONE);
                                                btnAddCart.setVisibility(View.GONE);
                                                item_sold_out.setVisibility(View.VISIBLE);
                                                item_sold_out.setText(message);
                                            } else {
                                                btnBuyNow.setVisibility(View.VISIBLE);
                                                btnAddCart.setVisibility(View.VISIBLE);
                                                item_sold_out.setVisibility(View.GONE);
                                            }
                                        }

                                        txtPinCode.setText((Html.fromHtml(message + " <font color='#0787EA'> <b>" + strPinCode + "</b></font>")));
                                    }
                                });

                            } else {
                                showErrorMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorMessage("Problem while adding products into the cart. Try again later");
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorMessage("Problem while adding product into the cart. Try again later");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        Volley.newRequestQueue(activity).add(jsonObjReq);
    }

    private void showErrorMessage(String message) {
        CommonDataUtility.showSnackBar(rl_main, message);
        dismissProgress();
    }
}
