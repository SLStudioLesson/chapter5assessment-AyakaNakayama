package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.taskapp.model.User;

public class UserDataAccess {
    private final String filePath;

    public UserDataAccess() {
        filePath = "app/src/main/resources/users.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     */
    public UserDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * メールアドレスとパスワードを基にユーザーデータを探します。
     * @param email メールアドレス
     * @param password パスワード
     * @return 見つかったユーザー
     */
    public User findByEmailAndPassword(String email, String password) {
        // Userインスタンスを作成
        User user = null;
        // users.csvファイルを読み込む
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // ヘッダーを飛ばす
            reader.readLine();
            // 一行ずつ読み込む
            String line;
            while ((line = reader.readLine()) != null) {
                // 一行をカンマで分解
                String[] values = line.split(",");
                
                // データが欠損している場合はスキップ
                if (values.length != 4) continue;
                
                // メールアドレスとパスワードが一致していない場合はスキップ
                String csvEmail = values[2];
                String csvPassword = values[3];
                if (!(email.equals(csvEmail) && password.equals(csvPassword))) continue;

                // データを基にUserインスタンスを作成
                int csvCode = (Integer.parseInt(values[0]));
                String csvName = values[1];

                user = new User(csvCode, csvName, csvEmail, csvPassword);
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return user;
    }

    /**
     * コードを基にユーザーデータを取得します。
     * @param code 取得するユーザーのコード
     * @return 見つかったユーザー
     */
    public User findByCode(int code) {
        // 空のUserインスタンスを作成
        User user = null;
        // ファイル読み込み
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // ヘッダーを飛ばす
            reader.readLine();

            // 一行ずつ読み込む
            String line;
            while ((line = reader.readLine()) != null) {
                // 一行をカンマで区切ってデータのリストを取得
                String[] values = line.split(",");

                // データが欠損している場合はスキップ
                if (values.length != 4) continue;

                // codeが一致していない場合はスキップ
                int csvCode = (Integer.parseInt(values[0]));
                if (code != csvCode) continue;
                // データを基にUserインスタンスを作成
                String csvName = values[1];
                String csvEmail = values[2];
                String csvPassword = values[3];

                user = new User(csvCode, csvName, csvEmail, csvPassword);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }
}
