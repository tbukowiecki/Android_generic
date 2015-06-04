package com.storefrontsdk.example;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Switch;

import com.storefrontsdk.Environment;
import com.storefrontsdk.ShopActivity_;


public class MainActivity extends ActionBarActivity {

    private Switch environmentSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        environmentSwitch= (Switch) findViewById(R.id.environment);
        openShop(R.id.shop_my_button, 61, "Xo9kvBrWd9f9plGUHpnjQuVoeMjN96Ap7Jc4");
        openShop(R.id.shop_button, 2, "x8JanABUNgoZd1KVbXKpuRtIUt6iycjjhFbU");
        openShop(R.id.shop_panda_button, 30, "KG5hO75oLZOzuxEosqg6Ok99HBAHmCciBXBG");
        openShop(R.id.shop_bestseller_button, 35, "nnPGfHIC6TRbngKlpGXL0wQezpXBrkZjLDm3");
        openShop(R.id.shop_new_this_week_button, 36, "SdCg9tyxCnC9fbhhjr3T52XDv6xI9mT9T7pO");
        openShop(R.id.shop_geek_and_nerd_button, 37, "rvV48nTRMJEcC6iRCxcfENf8Gb6EddjM6syy");
        openShop(R.id.shop_movies_button, 38, "m20IDg1JNY6RMmFumAEWEODiGAYlNMzqrTHY");
        openShop(R.id.shop_text_based_button, 39, "febCfeGwPDaicm5i9KYbq8hp0ykHGKMa622q");
        openShop(R.id.shop_pop_button, 40, "yIZl8uslXcz7rT6R5WsWOmFR2GY3yVRr3l4a");
        openShop(R.id.shop_sale_button, 48, "uYvQl9F22ieNcyNnJdm0yVLAZft5cOGjPO5N");
        openShop(R.id.shop_zombies_button, 41, "eFxNZSYMEcPYx0B4IuIvNeo8BBq7qdsfw8lf");
        openShop(R.id.shop_tv_button, 42, "8BbL66KWi6FWFq0kiEbOVH7ZI8RrXpq3wHp1");
        openShop(R.id.shop_graphic_button, 43, "IKewcbBqTITV2fkjtM0biKsTjuWcR6hVhWTK");
        openShop(R.id.shop_animals_button, 44, "iUeSB27vDs0yIfMKbyixHUsdOS18EBpxnr2I");
        openShop(R.id.shop_games_button, 45, "ZBfXvMqY690TkpejGjYF9yo8QiUax8nS2P7j");
        openShop(R.id.shop_bacon_button, 46, "BOgLdHeDuOjEW5K7MaYgSkG6HyRDhvOAQ0UE");
        openShop(R.id.shop_math_button, 47, "YA3D85QvvzXuD0BS4mrW2oCvjyQc37m4FdZ9");
    }

    protected void openShop(int id, final int projectId, final String projectSecret) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopActivity_.intent(MainActivity.this)
                        .sdkProjectId(projectId)
                        .sdkProjectSecret(projectSecret)
                        .environment(environmentSwitch.isChecked() ? Environment.PRODUCTION : Environment.SANDBOX)
                        .start();
            }
        });
    }

}
