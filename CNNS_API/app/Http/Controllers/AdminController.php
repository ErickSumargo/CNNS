<?php

namespace App\Http\Controllers;

use App\Helpers\Base;
use App\Models\Admin;

use App\Models\News;
use App\Models\NewsLike;
use App\Models\Report;
use App\Models\User;
use App\Models\UserNews;
use App\Models\View;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class AdminController extends Controller
{
    use Base;

    public function login(Request $req)
    {
        $user = Admin::where('email', $req['email'])->first();
        if ($user != null) {
            if (Hash::check($req['password'], $user->password)) {
                $data = new \stdClass();
                $data->user = $user;
                $data->user->type = 'admin';
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

    public function loadViews()
    {
        $views = View::where('updated_at', '>=', Carbon::today()->toDateString())->get();
        $counts = 0;
        foreach ($views as $view) {
            $counts += $view->views;
        }

        $data = new \stdClass();
        $data->views = $counts;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function loadMembers()
    {
        $users = User::where('active', 1)
            ->orderBy('created_at', 'desc')
            ->get();
        foreach ($users as $user) {
            $reputation = $this->getReputation($user->id);
            $user->ups = $reputation[0];
            $user->downs = $reputation[1];
        }

        $data = new \stdClass();
        $data->users = $users;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function loadNews()
    {
        $news = News::orderBy('created_at', 'desc')->get();
        foreach ($news as $n) {
            $n->user = $n->users[0];
            $n->ups = $n->likes->where('like', 1)->count();
            $n->downs = $n->likes->where('like', 0)->count();
            $n->comments;

            $views = 0;
            foreach ($n->views as $view) {
                $views += $view->views;
            }
            unset($n->views);
            $n->views = $views;

            unset($n->likes);
            unset($n->user->pivot);
            unset($n->users);
        }

        $data = new \stdClass();
        $data->newsList = $news;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function loadViewers(Request $req)
    {
        $views = View::where('news_id', $req['news_id'])->get();

        $users = [];
        foreach ($views as $view) {
            $user = User::find($view->user_id);
            $user->views = $view->views;

            $users[] = $user;
        }

        $data = new \stdClass();
        $data->users = $users;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function loadReportedNews()
    {
        $news = News::where('status', 2)
            ->orderBy('created_at', 'desc')
            ->get();
        $reports = [];
        foreach ($news as $n) {
            if ($n->reports->count() >= 1) {
                $n->rps = $n->reports->count();
                $n->user = $n->users[0];
                $n->ups = $n->likes->where('like', 1)->count();
                $n->downs = $n->likes->where('like', 0)->count();
                $n->comments;

                $views = 0;
                foreach ($n->views as $view) {
                    $views += $view->views;
                }
                unset($n->views);
                $n->views = $views;

                unset($n->reports);
                unset($n->likes);
                unset($n->user->pivot);
                unset($n->users);

                $reports[] = $n;
            }
        }
        usort($reports, function ($a, $b) {
            return $a->rps > $b->reports ? -1 : 1;
        });

        $data = new \stdClass();
        $data->newsList = $reports;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function loadReporters(Request $req)
    {
        $reports = Report::where('news_id', $req['news_id'])->get();

        $users = [];
        foreach ($reports as $report) {
            $user = User::find($report->user_id);
            $reputation = $this->getReputation($user->id);
            $user->ups = $reputation[0];
            $user->downs = $reputation[1];

            $users[] = $user;
        }

        $data = new \stdClass();
        $data->users = $users;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function loadUsers()
    {
        $users = User::where('active', 1)
            ->orderBy('created_at', 'desc')
            ->get();
        foreach ($users as $user) {
            $reputation = $this->getReputation($user->id);
            $user->ups = $reputation[0];
            $user->downs = $reputation[1];
        }

        $data = new \stdClass();
        $data->users = $users;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function blockUser(Request $req)
    {
        $user = User::where('id', $req['id'])->first();
        $user->active = 0;
        $user->save();

        return $this->result();
    }

    public function loadNewsDetail(Request $req)
    {
        $news = News::where('id', $req['news_id'])->first();

        $data = new \stdClass();
        $data->news = $news;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function postNews(Request $req)
    {
        $news = News::where('id', $req['news_id'])->first();
        $news->status = 2;
        $news->save();

        return $this->result();
    }

    public function editNews(Request $req)
    {
        $news = News::find($req['news_id']);
        $news->title = $req['title'];
        $news->content = $req['content'];
        $news->date = $req['date_time'];

        if ($req['image_changed'] == '1') {
            $this->deleteImage($news->image, 'news');
            if ($req->file('image') != null) {
                $content = $news->id;
                $news->image = $this->getImageName($req->file('image'), $content, 'news');
            } else {
                $news->image = '';
            }
        }
        if ($req['video_changed'] == '1') {
            $this->deleteVideo($news->video);
            if ($req->file('video') != null) {
                $content = $news->id;
                $news->video = $this->getVideoName($req->file('video'), $content);
            } else {
                $news->video = '';
            }
        }
        $news->save();

        return $this->result();
    }

    public function blockNews(Request $req)
    {
        $news = News::where('id', $req['news_id'])->first();
        $news->status = 0;
        $news->save();

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