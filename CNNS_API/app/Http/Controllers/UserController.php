<?php

namespace App\Http\Controllers;

use App\Models\NewsLike;
use App\Models\UserNews;
use Illuminate\Http\Request;
use App\Models\Registration;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

use App\Helpers\Base;

class UserController extends Controller
{
    use Base;

    public function registerPhone(Request $req)
    {
        $user = User::where('phone', $req['phone'])->first();
        if ($user == null) {
            $code = rand(0, 9999);
            $code .= '';
            while (strlen($code) < 4) {
                $code .= '0';
            }

            $reg = Registration::where('phone', $req['phone'])->first();
            if ($reg == null) {
                $reg_data = [
                    'phone' => $req['phone'],
                    'code' => $code
                ];
                Registration::store($reg_data);

                $data = new \stdClass();
                $data->code = $code;
                $this->response['data'] = $data;
            } else {
                if ($reg->status == 1) {
                    $data = new \stdClass();
                    $data->skipped = true;
                    $this->response['data'] = $data;
                } else {
                    $reg->code = $code;
                    $reg->save();

                    $data = new \stdClass();
                    $data->code = $code;
                    $this->response['data'] = $data;
                }
            }
        } else {
            $this->response['success'] = false;
            $this->response['error'] = 0;
        }
        return $this->result();
    }

    public function resendCode(Request $req)
    {
        $code = rand(0, 9999);
        $code .= '';
        while (strlen($code) < 4) {
            $code .= '0';
        }

        $reg = Registration::where('phone', $req['phone'])->first();
        $reg->code = $code;
        $reg->save();

        $data = new \stdClass();
        $data->code = $code;
        $this->response['data'] = $data;

        return $this->result();
    }

    public function verifyCode(Request $req)
    {
        $reg = Registration::where([
            ['phone', $req['phone']],
            ['code', $req['code']],
        ])
            ->first();

        if ($reg != null) {
            $reg->status = 1;
            $reg->save();
        } else {
            $this->response['success'] = false;
            $this->response['error'] = 0;
        }
        return $this->result();
    }

    public function registerUser(Request $req)
    {
        $reg = Registration::where([
            ['phone', $req['phone']],
            ['status', 1]
        ])
            ->first();

        if ($reg != null) {
            $user = User::where('email', $req['email'])->first();
            if ($user == null) {
                $user_data = [
                    'phone' => $req['phone'],
                    'name' => $req['name'],
                    'email' => $req['email'],
                    'password' => Hash::make($req['password']),
                    'birthday' => $req['birthday'],
                    'gender' => $req['gender'],
                    'city' => $req['city']
                ];
                $user = User::store($user_data);

                $data = new \stdClass();
                $data->user = $user;
                $data->user->ups = 0;
                $data->user->downs = 0;
                $data->user->type = 'user';
                $data->token = $this->token->getToken($user);

                $this->response['data'] = $data;
            } else {
                $this->response['success'] = false;
                $this->response['error'] = 1;
            }
        } else {
            $this->response['success'] = false;
            $this->response['error'] = 0;
        }
        return $this->result();
    }

    public function login(Request $req)
    {
        $user = User::where('email', $req['email'])->first();
        if ($user != null) {
            if (Hash::check($req['password'], $user->password)) {
                $data = new \stdClass();
                $data->user = $user;
                $data->user->type = 'user';
                $data->token = $this->token->getToken($user);

                $reputation = $this->getReputation($user->id);
                $data->user->ups = $reputation[0];
                $data->user->downs = $reputation[1];

                $this->response['data'] = $data;
            } else {
                $this->response['success'] = false;
                $this->response['error'] = 1;
            }
        } else {
            $this->response['success'] = false;
            $this->response['error'] = 0;
        }
        return $this->result();
    }

    public function updateProfile(Request $req)
    {
        $user = $this->getUser();
        if (isset($req['frequently'])) {
            $data = new \stdClass();
            $data->user = $user;
            $data->user->type = 'user';
            $data->token = $this->token->getToken($user);

            $reputation = $this->getReputation($user->id);
            $data->user->ups = $reputation[0];
            $data->user->downs = $reputation[1];

            $this->response['data'] = $data;
        } else {
            $phone_existed = User::where([
                ['id', '!=', $user->id],
                ['phone', $req['phone']]
            ])
                ->first();
            if ($phone_existed == null) {
                $user->gender = $req['gender'];
                $user->phone = $req['phone'];
                $user->address = $req['address'];

                if (isset($req['photo_changed'])) {
                    $this->deleteImage($user->photo, $this->getUserType());
                    if ($req->file('image') != null) {
                        $content = $user->id;
                        $user->photo = $this->getImageName($req->file('image'), $content, $this->getUserType());
                    } else {
                        $user->photo = '';
                    }
                }
                $user->save();

                $data = new \stdClass();
                $data->user = $user;
                $data->user->type = 'user';
                $data->token = $this->token->getToken($user);

                $reputation = $this->getReputation($user->id);
                $data->user->ups = $reputation[0];
                $data->user->downs = $reputation[1];

                $this->response['data'] = $data;
            } else {
                $this->response['success'] = false;
                $this->response['error'] = 0;
            }
        }
        return $this->result();
    }

    public function getReputation($user_id)
    {
        $news = UserNews::where('user_id', $user_id)->get();
        $ups = 0;
        $downs = 0;
        foreach ($news as $n) {
            $ups += NewsLike::where([
                ['news_id', $n->news_id],
                ['like', 1]
            ])
                ->count();

            $downs += NewsLike::where([
                ['news_id', $n->news_id],
                ['like', 0]
            ])
                ->count();
        }
        return [$ups, $downs];
    }
}