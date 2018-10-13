<?php
/**
 * Created by PhpStorm.
 * User: Erick Sumargo
 * Date: 5/28/2018
 * Time: 8:48 AM
 */

namespace App\Helpers;


trait Base
{
    protected $response;
    protected $token;

    public function __construct()
    {
        $this->response['success'] = true;
        $this->response['error'] = -1;
        $this->response['data'] = new \stdClass();

        $this->token = new Token();
    }

    public function result()
    {
        return $this->response;
    }

    public function getUser()
    {
        return $this->token->getUser();
    }

    public function getUserType()
    {
        return $this->token->getUserType();
    }

    public function getImageName($image, $content, $typePath)
    {
        $name = time() . '-' . $content . '.' . $image->getClientOriginalExtension();
        $image->move(base_path() . '/public/media/image/' . $typePath . '/', $name);

        return $name;
    }

    public function deleteImage($name, $typePath)
    {
        $file = base_path() . '/public/media/image/' . $typePath . '/' . $name;
        if (!is_dir($file) && is_file($file)) {
            unlink($file);
        }
    }

    public function getVideoName($video, $content)
    {
        $name = time() . '-' . $content . '.' . $video->getClientOriginalExtension();
        $video->move(base_path() . '/public/media/video/', $name);

        return $name;
    }

    public function deleteVideo($name)
    {
        $file = base_path() . '/public/media/video/' . $name;
        if (!is_dir($file) && is_file($file)) {
            unlink($file);
        }
    }
}