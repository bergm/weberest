goog.provide('webfui.dom_manipulation');
goog.require('cljs.core');
goog.require('webfui.html');
goog.require('webfui.html');
webfui.dom_manipulation.path_dom = (function path_dom(node){
return cljs.core.drop.call(null,3,cljs.core.reverse.call(null,(function f(node__$1){
return (new cljs.core.LazySeq(null,false,(function (){
if(cljs.core.truth_(node__$1))
{return cljs.core.cons.call(null,(cljs.core.count.call(null,cljs.core.take_while.call(null,cljs.core.identity,cljs.core.iterate.call(null,(function (p1__31179_SHARP_){
return p1__31179_SHARP_.previousSibling;
}),node__$1))) - 1),f.call(null,node__$1.parentNode));
} else
{return null;
}
}),null));
}).call(null,node)));
});
webfui.dom_manipulation.select_path_html = (function select_path_html(html,path){
while(true){
var temp__4090__auto__ = cljs.core.seq.call(null,path);
if(temp__4090__auto__)
{var vec__31181 = temp__4090__auto__;
var cur = cljs.core.nth.call(null,vec__31181,0,null);
var more = cljs.core.nthnext.call(null,vec__31181,1);
{
var G__31182 = cljs.core.nth.call(null,html.children,cur);
var G__31183 = more;
html = G__31182;
path = G__31183;
continue;
}
} else
{return html;
}
break;
}
});
webfui.dom_manipulation.resolve_target = (function resolve_target(parsed_html,target){
var path = webfui.dom_manipulation.path_dom.call(null,target);
var parsed_element = webfui.dom_manipulation.select_path_html.call(null,parsed_html,path);
return webfui.html.unparse_html.call(null,parsed_element);
});
