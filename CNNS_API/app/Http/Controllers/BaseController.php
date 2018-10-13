<?php

namespace App\Http\Controllers;

use App\Models\News;
use Illuminate\Http\Request;
use App\Helpers\Base;

class BaseController extends Controller
{
    use Base;

    public function estConn()
    {
        return $this->result();
    }

    public function loadBase()
    {
        $news = News::where('status', 2)
            ->orderBy('created_at', 'desc')
            ->get();
        $tops = [];
        foreach ($news as $n) {
            $n->user = $n->users[0];
            $n->ups = $n->likes->where('like', 1)->count();
            $n->downs = $n->likes->where('like', 0)->count();
            $n->comments;

            unset($n->likes);
            unset($n->user->pivot);
            unset($n->users);

            $tops[] = $n;
        }
        usort($tops, function ($a, $b) {
            return $a->ups > $b->ups ? -1 : 1;
        });

        $daily = News::where('status', 2)
            ->orderBy('created_at', 'desc')
            ->take(10)
            ->get();
        foreach ($daily as $n) {
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
        $data->tops = array_slice($tops, 0, 5);
        $data->daily = $daily;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function search(Request $req)
    {
        $news = News::where([
            ['title', 'like', '%' . $req['query'] . '%'],
            ['status', 2]
        ])
            ->orderBy('created_at', 'desc')
            ->take(5)
            ->get();
        foreach ($news as $n) {
            $n->user = $n->users[0];

            unset($n->user->pivot);
            unset($n->users);
        }

        $data = new \stdClass();
        $data->search_results = $news;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function loadMostPopular()
    {
        $news = News::where('status', 2)
            ->orderBy('created_at', 'desc')
            ->get();
        $tops = [];
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

            $tops[] = $n;
        }
        usort($tops, function ($a, $b) {
            return $a->ups > $b->ups ? -1 : 1;
        });

        $data = new \stdClass();
        $data->newsList = $tops;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function loadReported()
    {
        $news = News::where('status', 2)
            ->orderBy('created_at', 'desc')
            ->get();
        $reports = [];
        foreach ($news as $n) {
            if ($n->reports->count() >= 3) {
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

    public function loadLocations()
    {
        $news = News::where('status', 2)
            ->orderBy('created_at', 'desc')
            ->get();

        $data = new \stdClass();
        $data->newsList = $news;

        $this->response['data'] = $data;
        return $this->result();
    }
}