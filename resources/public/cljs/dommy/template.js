goog.provide('dommy.template');
goog.require('cljs.core');
goog.require('clojure.string');
dommy.template.add_class_BANG_ = (function add_class_BANG_(node,c){
return node.setAttribute("class",(function (){var temp__3971__auto__ = node.getAttribute("class");
if(cljs.core.truth_(temp__3971__auto__))
{var cur_c = temp__3971__auto__;
return [cljs.core.str(cur_c),cljs.core.str(" "),cljs.core.str(c)].join('');
} else
{return c;
}
})());
});
dommy.template.style_str = (function style_str(m){
return clojure.string.join.call(null," ",cljs.core.map.call(null,(function (p__1675){
var vec__1676 = p__1675;
var k = cljs.core.nth.call(null,vec__1676,0,null);
var v = cljs.core.nth.call(null,vec__1676,1,null);
return [cljs.core.str(cljs.core.name.call(null,k)),cljs.core.str(":"),cljs.core.str(cljs.core.name.call(null,v)),cljs.core.str(";")].join('');
}),m));
});
/**
* can have a seq for :classes key or a map for :style
*/
dommy.template.add_attrs_BANG_ = (function add_attrs_BANG_(node,attrs){
var G__1681 = cljs.core.seq.call(null,attrs);
while(true){
if(G__1681)
{var vec__1682 = cljs.core.first.call(null,G__1681);
var k = cljs.core.nth.call(null,vec__1682,0,null);
var v = cljs.core.nth.call(null,vec__1682,1,null);
var G__1683_1685 = k;
if(cljs.core._EQ_.call(null,"\uFDD0'style",G__1683_1685))
{node.setAttribute(cljs.core.name.call(null,k),dommy.template.style_str.call(null,v));
} else
{if(cljs.core._EQ_.call(null,"\uFDD0'classes",G__1683_1685))
{var G__1684_1686 = cljs.core.seq.call(null,v);
while(true){
if(G__1684_1686)
{var c_1687 = cljs.core.first.call(null,G__1684_1686);
dommy.template.add_class_BANG_.call(null,node,c_1687);
{
var G__1688 = cljs.core.next.call(null,G__1684_1686);
G__1684_1686 = G__1688;
continue;
}
} else
{}
break;
}
} else
{if(cljs.core._EQ_.call(null,"\uFDD0'class",G__1683_1685))
{dommy.template.add_class_BANG_.call(null,node,v);
} else
{if("\uFDD0'else")
{node.setAttribute(cljs.core.name.call(null,k),v);
} else
{}
}
}
}
{
var G__1689 = cljs.core.next.call(null,G__1681);
G__1681 = G__1689;
continue;
}
} else
{return null;
}
break;
}
});
dommy.template.next_css_index = (function next_css_index(s,start_idx){
var id_idx = s.indexOf("#",start_idx);
var class_idx = s.indexOf(".",start_idx);
var idx = Math.min(id_idx,class_idx);
if((idx < 0))
{return Math.max(id_idx,class_idx);
} else
{return idx;
}
});
/**
* dom element from css-style keyword like :a.class1 or :span#my-span.class
*/
dommy.template.base_element = (function base_element(node_key){
var node_str = cljs.core.name.call(null,node_key);
var base_idx = dommy.template.next_css_index.call(null,node_str,0);
var tag = (((base_idx > 0))?node_str.substring(0,base_idx):node_str);
var node = document.createElement(tag);
if((base_idx >= 0))
{var str_1692 = node_str.substring(base_idx);
while(true){
var next_idx_1693 = dommy.template.next_css_index.call(null,str_1692,1);
var frag_1694 = (((next_idx_1693 >= 0))?str_1692.substring(0,next_idx_1693):str_1692);
var G__1691_1695 = frag_1694.charAt(0);
if(cljs.core._EQ_.call(null,"#",G__1691_1695))
{node.setAttribute("id",frag_1694.substring(1));
} else
{if(cljs.core._EQ_.call(null,".",G__1691_1695))
{dommy.template.add_class_BANG_.call(null,node,frag_1694.substring(1));
} else
{if("\uFDD0'else")
{throw (new Error([cljs.core.str("No matching clause: "),cljs.core.str(frag_1694.charAt(0))].join('')));
} else
{}
}
}
if((next_idx_1693 >= 0))
{{
var G__1696 = str_1692.substring(next_idx_1693);
str_1692 = G__1696;
continue;
}
} else
{}
break;
}
} else
{}
return node;
});
dommy.template.element_QMARK_ = (function element_QMARK_(data){
var or__3824__auto__ = cljs.core.keyword_QMARK_.call(null,data);
if(or__3824__auto__)
{return or__3824__auto__;
} else
{var or__3824__auto____$1 = (function (){var and__3822__auto__ = cljs.core.coll_QMARK_.call(null,data);
if(and__3822__auto__)
{return cljs.core.keyword_QMARK_.call(null,cljs.core.first.call(null,data));
} else
{return and__3822__auto__;
}
})();
if(cljs.core.truth_(or__3824__auto____$1))
{return or__3824__auto____$1;
} else
{return cljs.core.instance_QMARK_.call(null,HTMLElement,data);
}
}
});
dommy.template.node_QMARK_ = (function node_QMARK_(data){
var or__3824__auto__ = dommy.template.element_QMARK_.call(null,data);
if(cljs.core.truth_(or__3824__auto__))
{return or__3824__auto__;
} else
{var or__3824__auto____$1 = cljs.core.string_QMARK_.call(null,data);
if(or__3824__auto____$1)
{return or__3824__auto____$1;
} else
{var or__3824__auto____$2 = cljs.core.number_QMARK_.call(null,data);
if(or__3824__auto____$2)
{return or__3824__auto____$2;
} else
{return cljs.core.instance_QMARK_.call(null,Text,data);
}
}
}
});
/**
* element with either attrs or nested children [:div [:span "Hello"]]
*/
dommy.template.compound_element = (function compound_element(data){
var n = dommy.template.base_element.call(null,cljs.core.first.call(null,data));
var attrs = ((cljs.core.map_QMARK_.call(null,cljs.core.second.call(null,data)))?cljs.core.second.call(null,data):null);
var tail = cljs.core.drop.call(null,(cljs.core.truth_(attrs)?2:1),data);
var tail__$1 = cljs.core.mapcat.call(null,(function (group){
if(cljs.core.truth_(dommy.template.node_QMARK_.call(null,group)))
{return cljs.core.PersistentVector.fromArray([group], true);
} else
{return group;
}
}),tail);
if(cljs.core.truth_(attrs))
{dommy.template.add_attrs_BANG_.call(null,n,attrs);
} else
{}
var G__1698_1699 = cljs.core.seq.call(null,tail__$1);
while(true){
if(G__1698_1699)
{var child_1700 = cljs.core.first.call(null,G__1698_1699);
n.appendChild(dommy.template.node.call(null,child_1700));
{
var G__1701 = cljs.core.next.call(null,G__1698_1699);
G__1698_1699 = G__1701;
continue;
}
} else
{}
break;
}
return n;
});
dommy.template.element = (function element(data){
if(cljs.core.keyword_QMARK_.call(null,data))
{return dommy.template.base_element.call(null,data);
} else
{if((function (){var and__3822__auto__ = cljs.core.coll_QMARK_.call(null,data);
if(and__3822__auto__)
{return cljs.core.keyword_QMARK_.call(null,cljs.core.first.call(null,data));
} else
{return and__3822__auto__;
}
})())
{return dommy.template.compound_element.call(null,data);
} else
{if(cljs.core.instance_QMARK_.call(null,HTMLElement,data))
{return data;
} else
{if("\uFDD0'else")
{throw [cljs.core.str("Don't know how to make element from "),cljs.core.str(cljs.core.pr_str.call(null,data))].join('');
} else
{return null;
}
}
}
}
});
dommy.template.node = (function node(data){
if(cljs.core.truth_(dommy.template.element_QMARK_.call(null,data)))
{return dommy.template.element.call(null,data);
} else
{if((function (){var or__3824__auto__ = cljs.core.number_QMARK_.call(null,data);
if(or__3824__auto__)
{return or__3824__auto__;
} else
{return cljs.core.string_QMARK_.call(null,data);
}
})())
{return document.createTextNode([cljs.core.str(data)].join(''));
} else
{if(cljs.core.instance_QMARK_.call(null,Text,data))
{return data;
} else
{if("\uFDD0'else")
{throw [cljs.core.str("Don't know how to make node from "),cljs.core.str(cljs.core.pr_str.call(null,data))].join('');
} else
{return null;
}
}
}
}
});
dommy.template.html__GT_nodes = (function html__GT_nodes(html){
var parent = document.createElement("div");
parent.insertAdjacentHTML("beforeend",html);
return Array.prototype.slice.call(parent.childNodes);
});
