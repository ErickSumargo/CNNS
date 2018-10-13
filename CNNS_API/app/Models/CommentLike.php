<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class CommentLike extends Model
{
    protected $table = 'comments_likes';
    protected $fillable = ['user_id', 'comment_id', 'like'];

    public static function store($data)
    {
        return Static::create([
            'user_id' => $data['user_id'],
            'comment_id' => $data['comment_id'],
            'like' => $data['like']
        ]);
    }
}
