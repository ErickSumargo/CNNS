<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class UserNews extends Model
{
    protected $table = 'users_news';
    protected $fillable = ['user_id', 'news_id'];

    public static function store($data)
    {
        return Static::create([
            'user_id' => $data['user_id'],
            'news_id' => $data['news_id']
        ]);
    }

    public function news()
    {
        return $this->belongsTo('App\Models\News');
    }
}