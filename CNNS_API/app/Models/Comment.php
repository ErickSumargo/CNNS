<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Comment extends Model
{
    protected $fillable = ['user_id', 'news_id', 'content'];

    public static function store($data)
    {
        return Static::create([
            'user_id' => $data['user_id'],
            'news_id' => $data['news_id'],
            'content' => $data['content']
        ]);
    }

    public function user()
    {
        return $this->belongsTo('App\Models\User');
    }

    public function likes()
    {
        return $this->hasMany('App\Models\CommentLike');
    }
}
