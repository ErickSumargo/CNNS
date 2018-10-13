<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class News extends Model
{
    protected $fillable = ['title', 'content', 'latitude', 'longitude', 'image', 'video', 'date', 'status'];

    public static function store($data)
    {
        return Static::create([
            'title' => $data['title'],
            'content' => $data['content'],
            'latitude' => $data['latitude'],
            'longitude' => $data['longitude'],
            'date' => $data['date'],
            'status' => $data['status'],
        ]);
    }

    public function users()
    {
        return $this->belongsToMany('App\Models\User', 'users_news', 'news_id', 'user_id');
    }

    public function comments()
    {
        return $this->hasMany('App\Models\Comment', 'news_id', 'id');
    }

    public function likes()
    {
        return $this->hasMany('App\Models\NewsLike');
    }

    public function views()
    {
        return $this->hasMany('App\Models\View');
    }

    public function reports()
    {
        return $this->hasMany('App\Models\Report');
    }
}