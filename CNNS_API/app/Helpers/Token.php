<?php
/**
 * Created by PhpStorm.
 * User: Erick Sumargo
 * Date: 5/28/2018
 * Time: 8:48 AM
 */

namespace App\Helpers;

use App\Models\Admin;
use App\Models\User;

class Token
{
    protected $secret, $algo;

    protected static $user;
    protected static $user_type;

    function __construct()
    {
        $this->secret = 'M9dBdNRx2WXO8hPTe7Shp7yebOrulbrc1Jz9jm76';
        $this->algo = 'HS256';
    }

    public function validate($user_type, $token)
    {
        $data = JWT::decode($token, $this->secret, array($this->algo));

        self::$user_type = $user_type;
        switch ($user_type) {
            case 'user':
                self::$user = User::where([
                    ['id', '=', $data->id],
                    ['email', '=', $data->email],
                    ['phone', '=', $data->phone],
                    ['active', '=', 1]
                ])
                    ->first();
                break;
            case 'admin':
                self::$user = Admin::where([
                    ['id', '=', $data->id],
                    ['email', '=', $data->email]
                ])
                    ->first();
                break;
            case 'guest':
                self::$user = $data;
                break;
            default:
                break;
        }

        if (self::$user == null) {
            return false;
        }
        return true;
    }

    public function getToken($obj)
    {
        return JWT::encode($obj, $this->secret, $this->algo);
    }

    public function getUser()
    {
        return self::$user != null ? self::$user : null;
    }

    public function getUserType()
    {
        return self::$user_type;
    }
}