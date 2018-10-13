<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class View extends Model
{
    protected $fillable = ['user_id', 'news_id', 'views'];

    public static function store($data)
    {
        return Static::create([
            'user_id' => $data['user_id'],
            'news_id' => $data['news_id'],
            'views' => 1
        ]);
    }
}