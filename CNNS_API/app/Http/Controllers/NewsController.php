<?php

namespace App\Http\Controllers;

use App\Models\Comment;
use App\Models\CommentLike;
use App\Models\NewsLike;
use App\Models\Report;
use App\Models\User;
use App\Models\UserComment;
use App\Models\UserNews;
use App\Models\View;
use Illuminate\Http\Request;
use App\Models\News;
use App\Helpers\Base;

class NewsController extends Controller
{
    use Base;

    public function loadNewsDetail(Request $req)
    {
        $news = News::where('id', $req['news_id'])->first();
        if ($req['user_id'] != null) {
            $news_like = NewsLike::where([
                ['user_id', $req['user_id']],
                ['news_id', $news->id]
            ])
                ->first();
            $like = $news_like != null ? $news_like->like : -1;

            $report = Report::where([
                ['user_id', $req['user_id']],
                ['news_id', $news->id]
            ])
                ->first();
            $reported = $report != null ? 1 : 0;

            $user_news = UserNews::where([
                ['user_id', $req['user_id']],
                ['news_id', $news->id]
            ])
                ->first();
            $own = $user_news != null ? true : false;
        } else {
            $own = false;
            $like = -1;
        }

        $data = new \stdClass();
        $data->news = $news;
        $data->like = $like;
        $data->reported = $reported;
        $data->own = $own;

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

    public function loadDrafts()
    {
        $user = $this->getUser();
        $users_news = UserNews::where('user_id', $user->id)
            ->get();
        $news = [];
        foreach ($users_news as $user_news) {
            if ($user_news->news->status < 2) {
                $n = $user_news->news;
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

                $news[] = $n;
            }
        }

        $data = new \stdClass();
        $data->newsList = $news;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function loadDraftDetail(Request $req)
    {
        $news = News::find($req['news_id']);

        $data = new \stdClass();
        $data->news = $news;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function setViewed(Request $req)
    {
        $user = $this->getUser();
        $view = View::where([
            ['user_id', $user->id],
            ['news_id', $req['news_id']]
        ])
            ->first();
        if ($view == null) {
            $view_data = [
                'user_id' => $user->id,
                'news_id' => $req['news_id']
            ];
            View::store($view_data);
        } else {
            $view->views += 1;
            $view->save();
        }
        return $this->result();
    }

    public function setNewsLike(Request $req)
    {
        $user = $this->getUser();
        $news_like = NewsLike::where([
            ['user_id', $user->id],
            ['news_id', $req['news_id']],
        ])
            ->first();
        if ($news_like == null) {
            $news_like_data = [
                'user_id' => $user->id,
                'news_id' => $req['news_id'],
                'like' => $req['like']
            ];
            NewsLike::store($news_like_data);
        } else {
            $news_like->like = $req['like'];
            $news_like->save();
        }
        return $this->result();
    }

    public function report(Request $req)
    {
        $user = $this->getUser();
        $report = Report::where([
            ['user_id', $user->id],
            ['news_id', $req['news_id']],
        ])
            ->first();
        if ($report == null) {
            $report_data = [
                'user_id' => $user->id,
                'news_id' => $req['news_id']
            ];
            Report::store($report_data);
        }
        return $this->result();
    }

    public function loadComments(Request $req)
    {
        $news = News::where('id', $req['news_id'])->first();

        $comments = Comment::where('news_id', $news->id)
            ->get();
        foreach ($comments as $comment) {
            $comment->user;
            if ($req['user_id'] != null) {
                $comments_like = CommentLike::where([
                    ['user_id', $req['user_id']],
                    ['comment_id', $comment->id]
                ])
                    ->first();
                $like = $comments_like != null ? $comments_like->like : -1;
            } else {
                $like = -1;
            }
            $comment->like = $like;

            $comment->ups = $comment->likes->where('like', 1)->count();
            $comment->downs = $comment->likes->where('like', 0)->count();

            unset($comment->likes);
        }

        $data = new \stdClass();
        $data->news = $news;
        $data->comments = $comments;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function setCommentLike(Request $req)
    {
        $user = $this->getUser();
        $comment_like = CommentLike::where([
            ['user_id', $user->id],
            ['comment_id', $req['comment_id']],
        ])
            ->first();
        if ($comment_like == null) {
            $comment_like_data = [
                'user_id' => $user->id,
                'comment_id' => $req['comment_id'],
                'like' => $req['like']
            ];
            CommentLike::store($comment_like_data);
        } else {
            $comment_like->like = $req['like'];
            $comment_like->save();
        }
        return $this->result();
    }

    public function postComment(Request $req)
    {
        $user = $this->getUser();
        $comment_data = [
            'user_id' => $user->id,
            'news_id' => $req['news_id'],
            'content' => $req['content']
        ];
        $comment = Comment::store($comment_data);

        $user_comment_data = [
            'user_id' => $user->id,
            'comment_id' => $comment->id
        ];
        UserComment::store($user_comment_data);

        return $this->result();
    }

    public function postNews(Request $req)
    {
        $user = $this->getUser();
        $news_data = [
            'title' => $req['title'],
            'content' => $req['content'],
            'latitude' => $req['latitude'],
            'longitude' => $req['longitude'],
            'date' => $req['date_time'],
            'status' => 1
        ];
        $news = News::store($news_data);

        if (isset($req['image'])) {
            if ($req->file('image') != null) {
                $content = $news->id;
                $news->image = $this->getImageName($req->file('image'), $content, 'news');
            } else {
                $news->image = '';
            }
        } else {
            $news->image = '';
        }
        if (isset($req['video'])) {
            if ($req->file('video') != null) {
                $content = $news->id;
                $news->video = $this->getVideoName($req->file('video'), $content);
            } else {
                $news->video = '';
            }
        } else {
            $news->video = '';
        }
        $news->save();

        $user_news_data = [
            'user_id' => $user->id,
            'news_id' => $news->id
        ];
        UserNews::store($user_news_data);

        return $this->result();
    }

    public function editNews(Request $req)
    {
        $news = News::find($req['news_id']);
        $news->title = $req['title'];
        $news->content = $req['content'];
        $news->latitude = $req['latitude'];
        $news->longitude = $req['longitude'];
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

    public function loadVideos()
    {
        $news = News::where([
            ['video', '!=', ''],
            ['status', 2]
        ])
            ->orderBy('created_at', 'desc')
            ->get();
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

    public function loadBookmarks(Request $req)
    {
        $ids = explode(' ', $req['ids']);
        $bookmarks = [];
        foreach ($ids as $id) {
            $n = News::find($id);
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

            $bookmarks[] = $n;
        }

        $data = new \stdClass();
        $data->newsList = $bookmarks;

        $this->response['data'] = $data;
        return $this->result();
    }

    public function loadHistories(Request $req)
    {
        $users_news = UserNews::where('user_id', $req['id'])
            ->get();
        $news = [];
        foreach ($users_news as $user_news) {
            $n = $user_news->news;
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

            $news[] = $n;
        }

        $data = new \stdClass();
        $data->newsList = $news;

        $this->response['data'] = $data;
        return $this->result();
    }
}