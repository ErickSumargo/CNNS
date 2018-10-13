<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class UserComment extends Model
{
    protected $table = 'users_comments';
    protected $fillable = ['user_id', 'comment_id'];

    public static function store($data)
    {
        return Static::create([
            'user_id' => $data['user_id'],
            'comment_id' => $data['comment_id']
        ]);
    }
}
