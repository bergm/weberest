goog.provide('webfui.core');
goog.require('cljs.core');
goog.require('webfui.plugin.core');
goog.require('cljs.reader');
goog.require('webfui.html');
goog.require('cljs.reader');
goog.require('webfui.plugin.core');
goog.require('webfui.html');
webfui.core.body = (function body(){
return document.body;
});
webfui.core.select_path_dom = (function select_path_dom(node,path){
while(true){
var temp__4090__auto__ = cljs.core.seq.call(null,path);
if(temp__4090__auto__)
{var vec__31252 = temp__4090__auto__;
var cur = cljs.core.nth.call(null,vec__31252,0,null);
var more = cljs.core.nthnext.call(null,vec__31252,1);
{
var G__31253 = node.childNodes.item(cur);
var G__31254 = more;
node = G__31253;
path = G__31254;
continue;
}
} else
{return node;
}
break;
}
});
webfui.core.dom_ready_QMARK_ = (function dom_ready_QMARK_(){
return cljs.core._EQ_.call(null,document.readyState,"complete");
});
webfui.core.dom_ready = (function dom_ready(fun){
return window.onload = fun;
});
webfui.core.parsed_html_watcher = (function parsed_html_watcher(key,a,old,new$){
var delta = webfui.html.html_delta.call(null,old.children,new$.children,cljs.core.PersistentVector.EMPTY);
var G__31259_31263 = cljs.core.seq.call(null,delta);
while(true){
if(G__31259_31263)
{var vec__31260_31264 = cljs.core.first.call(null,G__31259_31263);
var typ_31265 = cljs.core.nth.call(null,vec__31260_31264,0,null);
var path_31266 = cljs.core.nth.call(null,vec__31260_31264,1,null);
var a_31267__$1 = cljs.core.nth.call(null,vec__31260_31264,2,null);
var b_31268 = cljs.core.nth.call(null,vec__31260_31264,3,null);
var node_31269 = webfui.core.select_path_dom.call(null,webfui.core.body.call(null),path_31266);
var G__31261_31270 = typ_31265;
if(cljs.core._EQ_.call(null,"\uFDD0'html",G__31261_31270))
{node_31269.innerHTML = cljs.core.apply.call(null,cljs.core.str,cljs.core.map.call(null,webfui.html.html,a_31267__$1));
} else
{if(cljs.core._EQ_.call(null,"\uFDD0'rem-att",G__31261_31270))
{node_31269.removeAttribute(cljs.core.name.call(null,a_31267__$1));
} else
{if(cljs.core._EQ_.call(null,"\uFDD0'att",G__31261_31270))
{if(cljs.core._EQ_.call(null,a_31267__$1,"\uFDD0'value"))
{node_31269.value = [cljs.core.str(b_31268)].join('');
} else
{node_31269.setAttribute(cljs.core.name.call(null,a_31267__$1),webfui.html.render_attribute_value.call(null,a_31267__$1,b_31268));
}
} else
{if("\uFDD0'else")
{throw (new Error([cljs.core.str("No matching clause: "),cljs.core.str(typ_31265)].join('')));
} else
{}
}
}
}
{
var G__31271 = cljs.core.next.call(null,G__31259_31263);
G__31259_31263 = G__31271;
continue;
}
} else
{}
break;
}
var G__31262 = cljs.core.seq.call(null,cljs.core.deref.call(null,webfui.plugin.core.active_plugins));
while(true){
if(G__31262)
{var plugin = cljs.core.first.call(null,G__31262);
webfui.plugin.core.fix_dom.call(null,plugin);
{
var G__31272 = cljs.core.next.call(null,G__31262);
G__31262 = G__31272;
continue;
}
} else
{return null;
}
break;
}
});
webfui.core.parsed_html_atom = cljs.core.atom.call(null,(new webfui.html.parsed_html("\uFDD0'body",cljs.core.ObjMap.EMPTY,null)));
webfui.core.update_parsed_html_atom = (function update_parsed_html_atom(new$,old){
return (new webfui.html.parsed_html("\uFDD0'body",cljs.core.ObjMap.EMPTY,(((function (){var or__3943__auto__ = cljs.core.seq_QMARK_.call(null,new$);
if(or__3943__auto__)
{return or__3943__auto__;
} else
{return cljs.core.list_QMARK_.call(null,new$);
}
})())?webfui.html.parse_html.call(null,new$):webfui.html.parse_html.call(null,cljs.core.list.call(null,new$)))));
});
webfui.core.html_watcher = (function html_watcher(key,a,old,new$){
return cljs.core.swap_BANG_.call(null,webfui.core.parsed_html_atom,cljs.core.partial.call(null,webfui.core.update_parsed_html_atom,new$));
});
webfui.core.dom_watchers = cljs.core.atom.call(null,cljs.core.ObjMap.EMPTY);
webfui.core.core_add_dom_watch = (function core_add_dom_watch(id,fun){
return cljs.core.swap_BANG_.call(null,webfui.core.dom_watchers,cljs.core.assoc,id,fun);
});
webfui.core.init_dom = (function init_dom(html){
var b = webfui.core.body.call(null);
var G__31274_31275 = cljs.core.seq.call(null,cljs.core.deref.call(null,webfui.plugin.core.active_plugins));
while(true){
if(G__31274_31275)
{var plugin_31276 = cljs.core.first.call(null,G__31274_31275);
webfui.plugin.core.declare_events.call(null,plugin_31276,webfui.core.body.call(null),webfui.core.dom_watchers,webfui.core.parsed_html_atom);
{
var G__31277 = cljs.core.next.call(null,G__31274_31275);
G__31274_31275 = G__31277;
continue;
}
} else
{}
break;
}
cljs.core.add_watch.call(null,html,"\uFDD0'dom-watcher",webfui.core.html_watcher);
cljs.core.add_watch.call(null,webfui.core.parsed_html_atom,"\uFDD0'parsed-html-watcher",webfui.core.parsed_html_watcher);
return cljs.core.swap_BANG_.call(null,html,cljs.core.identity);
});
webfui.core.core_defdom = (function core_defdom(clj_dom){
if(cljs.core.truth_(webfui.core.dom_ready_QMARK_.call(null)))
{return webfui.core.init_dom.call(null,clj_dom);
} else
{return webfui.core.dom_ready.call(null,cljs.core.partial.call(null,webfui.core.init_dom,clj_dom));
}
});
