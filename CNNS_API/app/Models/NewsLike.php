<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class NewsLike extends Model
{
    protected $table = 'news_likes';
    protected $fillable = ['user_id', 'news_id', 'like'];

    public static function store($data)
    {
        return Static::create([
            'user_id' => $data['user_id'],
            'news_id' => $data['news_id'],
            'like' => $data['like']
        ]);
    }
}
