<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class User extends Model
{
    protected $fillable = ['email', 'password', 'name', 'birthday', 'address', 'city', 'gender', 'phone', 'photo', 'active'];
    protected $hidden = ['password', 'remember_token'];

    public static function store($data)
    {
        return Static::create([
            'email' => $data['email'],
            'password' => $data['password'],
            'name' => $data['name'],
            'birthday' => $data['birthday'],
            'gender' => $data['gender'],
            'city' => $data['city'],
            'address' => '',
            'phone' => $data['phone'],
            'photo' => ''
        ]);
    }
}