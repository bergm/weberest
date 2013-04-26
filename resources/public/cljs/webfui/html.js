goog.provide('webfui.html');
goog.require('cljs.core');
goog.require('clojure.set');
goog.require('clojure.set');
goog.provide('webfui.html.parsed_tagname');

/**
* @constructor
*/
webfui.html.parsed_tagname = (function (tagname,id,classes){
this.tagname = tagname;
this.id = id;
this.classes = classes;
})
webfui.html.parsed_tagname.cljs$lang$type = true;
webfui.html.parsed_tagname.cljs$lang$ctorPrSeq = (function (this__12537__auto__){
return cljs.core.list.call(null,"webfui.html/parsed-tagname");
});
webfui.html.parsed_tagname.cljs$lang$ctorPrWriter = (function (this__12537__auto__,writer__12538__auto__,opts__12539__auto__){
return cljs.core._write.call(null,writer__12538__auto__,"webfui.html/parsed-tagname");
});
goog.provide('webfui.html.parsed_html');

/**
* @constructor
*/
webfui.html.parsed_html = (function (tagname,attributes,children){
this.tagname = tagname;
this.attributes = attributes;
this.children = children;
})
webfui.html.parsed_html.cljs$lang$type = true;
webfui.html.parsed_html.cljs$lang$ctorPrSeq = (function (this__12537__auto__){
return cljs.core.list.call(null,"webfui.html/parsed-html");
});
webfui.html.parsed_html.cljs$lang$ctorPrWriter = (function (this__12537__auto__,writer__12538__auto__,opts__12539__auto__){
return cljs.core._write.call(null,writer__12538__auto__,"webfui.html/parsed-html");
});
webfui.html.parse_tagname = (function parse_tagname(tagname){
var vec__31185 = cljs.core.re_matches.call(null,/^([^.^#]+)(#([^.]+))?(\..+)?/,tagname);
var _ = cljs.core.nth.call(null,vec__31185,0,null);
var name = cljs.core.nth.call(null,vec__31185,1,null);
var ___$1 = cljs.core.nth.call(null,vec__31185,2,null);
var id = cljs.core.nth.call(null,vec__31185,3,null);
var classes = cljs.core.nth.call(null,vec__31185,4,null);
return (new webfui.html.parsed_tagname(cljs.core.keyword.call(null,name),(cljs.core.truth_(id)?cljs.core.keyword.call(null,id):null),(cljs.core.truth_(classes)?cljs.core.map.call(null,cljs.core.second,cljs.core.re_seq.call(null,/\.([^.]+)/,classes)):null)));
});
var cache_31186 = cljs.core.atom.call(null,cljs.core.ObjMap.EMPTY);
webfui.html.parse_tagname_memoized = (function parse_tagname_memoized(tagname){
var or__3943__auto__ = cljs.core.deref.call(null,cache_31186).call(null,tagname);
if(cljs.core.truth_(or__3943__auto__))
{return or__3943__auto__;
} else
{var val = webfui.html.parse_tagname.call(null,tagname);
cljs.core.swap_BANG_.call(null,cache_31186,cljs.core.assoc,tagname,val);
return val;
}
});
webfui.html.parse_element = (function parse_element(element){
var vec__31190 = element;
var tagname = cljs.core.nth.call(null,vec__31190,0,null);
var more = cljs.core.nthnext.call(null,vec__31190,1);
var parsed = webfui.html.parse_tagname_memoized.call(null,tagname);
var classes = parsed.classes;
var id = parsed.id;
var tagname__$1 = parsed.tagname;
var attributes = cljs.core.ObjMap.EMPTY;
var attributes__$1 = (cljs.core.truth_(classes)?cljs.core.assoc.call(null,attributes,"\uFDD0'class",cljs.core.apply.call(null,cljs.core.str,cljs.core.interpose.call(null," ",classes))):attributes);
var attributes__$2 = (cljs.core.truth_(id)?cljs.core.assoc.call(null,attributes__$1,"\uFDD0'id",id):attributes__$1);
var vec__31191 = more;
var a = cljs.core.nth.call(null,vec__31191,0,null);
var b = cljs.core.nthnext.call(null,vec__31191,1);
var vec__31192 = ((cljs.core.map_QMARK_.call(null,a))?cljs.core.PersistentVector.fromArray([cljs.core.merge.call(null,attributes__$2,a),b], true):cljs.core.PersistentVector.fromArray([attributes__$2,more], true));
var attributes__$3 = cljs.core.nth.call(null,vec__31192,0,null);
var children = cljs.core.nth.call(null,vec__31192,1,null);
return (new webfui.html.parsed_html(tagname__$1,attributes__$3,webfui.html.parse_html.call(null,children)));
});
webfui.html.merge_strings = (function merge_strings(lst){
var temp__4090__auto__ = lst;
if(cljs.core.truth_(temp__4090__auto__))
{var vec__31195 = temp__4090__auto__;
var x = cljs.core.nth.call(null,vec__31195,0,null);
var more = cljs.core.nthnext.call(null,vec__31195,1);
var temp__4090__auto____$1 = more;
if(cljs.core.truth_(temp__4090__auto____$1))
{var vec__31196 = temp__4090__auto____$1;
var y = cljs.core.nth.call(null,vec__31196,0,null);
var more__$1 = cljs.core.nthnext.call(null,vec__31196,1);
if((function (){var and__3941__auto__ = cljs.core.string_QMARK_.call(null,x);
if(and__3941__auto__)
{return cljs.core.string_QMARK_.call(null,y);
} else
{return and__3941__auto__;
}
})())
{return merge_strings.call(null,cljs.core.cons.call(null,[cljs.core.str(x),cljs.core.str(y)].join(''),more__$1));
} else
{return cljs.core.cons.call(null,x,merge_strings.call(null,cljs.core.cons.call(null,y,more__$1)));
}
} else
{return lst;
}
} else
{return lst;
}
});
webfui.html.parse_html = (function parse_html(html){
return cljs.core.mapcat.call(null,(function (x){
if(cljs.core.vector_QMARK_.call(null,x))
{return cljs.core.PersistentVector.fromArray([webfui.html.parse_element.call(null,x)], true);
} else
{if((function (){var and__3941__auto__ = cljs.core.coll_QMARK_.call(null,x);
if(and__3941__auto__)
{return !(cljs.core.string_QMARK_.call(null,x));
} else
{return and__3941__auto__;
}
})())
{return parse_html.call(null,x);
} else
{if("\uFDD0'else")
{return cljs.core.PersistentVector.fromArray([x], true);
} else
{return null;
}
}
}
}),webfui.html.merge_strings.call(null,html));
});
webfui.html.tag = (function tag(tagname,atts,s){
if(cljs.core.truth_(cljs.core.PersistentHashSet.fromArray(["\uFDD0'br"]).call(null,tagname)))
{return [cljs.core.str("<"),cljs.core.str(cljs.core.name.call(null,tagname)),cljs.core.str(atts),cljs.core.str(">")].join('');
} else
{return [cljs.core.str("<"),cljs.core.str(cljs.core.name.call(null,tagname)),cljs.core.str(atts),cljs.core.str(">"),cljs.core.str(cljs.core.apply.call(null,cljs.core.str,s)),cljs.core.str("</"),cljs.core.str(cljs.core.name.call(null,tagname)),cljs.core.str(">")].join('');
}
});
webfui.html.pixels = (function pixels(k){
return [cljs.core.str(k.toFixed(3)),cljs.core.str("px")].join('');
});
webfui.html.render_css = (function render_css(css){
return cljs.core.apply.call(null,cljs.core.str,cljs.core.interpose.call(null,";",(function (){var iter__12691__auto__ = (function iter__31201(s__31202){
return (new cljs.core.LazySeq(null,false,(function (){
var s__31202__$1 = s__31202;
while(true){
var temp__4092__auto__ = cljs.core.seq.call(null,s__31202__$1);
if(temp__4092__auto__)
{var xs__4579__auto__ = temp__4092__auto__;
var vec__31204 = cljs.core.first.call(null,xs__4579__auto__);
var k = cljs.core.nth.call(null,vec__31204,0,null);
var v = cljs.core.nth.call(null,vec__31204,1,null);
return cljs.core.cons.call(null,[cljs.core.str(cljs.core.name.call(null,k)),cljs.core.str(":"),cljs.core.str(((cljs.core.keyword_QMARK_.call(null,v))?cljs.core.name.call(null,v):(cljs.core.truth_((function (){var and__3941__auto__ = cljs.core.number_QMARK_.call(null,v);
if(and__3941__auto__)
{return cljs.core.PersistentHashSet.fromArray(["\uFDD0'bottom","\uFDD0'width","\uFDD0'top","\uFDD0'right","\uFDD0'left","\uFDD0'line-height","\uFDD0'height"]).call(null,k);
} else
{return and__3941__auto__;
}
})())?webfui.html.pixels.call(null,v):(("\uFDD0'else")?v:null))))].join(''),iter__31201.call(null,cljs.core.rest.call(null,s__31202__$1)));
} else
{return null;
}
break;
}
}),null));
});
return iter__12691__auto__.call(null,css);
})()));
});
webfui.html.render_attribute_value = (function render_attribute_value(key,value){
if(cljs.core.keyword_QMARK_.call(null,value))
{return cljs.core.name.call(null,value);
} else
{if(cljs.core._EQ_.call(null,key,"\uFDD0'data"))
{return cljs.core.print_str.call(null,value);
} else
{if(cljs.core._EQ_.call(null,key,"\uFDD0'style"))
{return webfui.html.render_css.call(null,value);
} else
{if("\uFDD0'else")
{return value;
} else
{return null;
}
}
}
}
});
webfui.html.render_attributes = (function render_attributes(atts){
return cljs.core.apply.call(null,cljs.core.str,(function (){var iter__12691__auto__ = (function iter__31209(s__31210){
return (new cljs.core.LazySeq(null,false,(function (){
var s__31210__$1 = s__31210;
while(true){
var temp__4092__auto__ = cljs.core.seq.call(null,s__31210__$1);
if(temp__4092__auto__)
{var xs__4579__auto__ = temp__4092__auto__;
var vec__31212 = cljs.core.first.call(null,xs__4579__auto__);
var key = cljs.core.nth.call(null,vec__31212,0,null);
var value = cljs.core.nth.call(null,vec__31212,1,null);
return cljs.core.cons.call(null,[cljs.core.str(" "),cljs.core.str(cljs.core.name.call(null,key)),cljs.core.str("=\""),cljs.core.str(webfui.html.render_attribute_value.call(null,key,value)),cljs.core.str("\"")].join(''),iter__31209.call(null,cljs.core.rest.call(null,s__31210__$1)));
} else
{return null;
}
break;
}
}),null));
});
return iter__12691__auto__.call(null,atts);
})());
});
webfui.html.html = (function html(content){
if(cljs.core.instance_QMARK_.call(null,webfui.html.parsed_html,content))
{var tagname = content.tagname;
var attributes = content.attributes;
var children = content.children;
if(cljs.core.truth_(tagname))
{return webfui.html.tag.call(null,tagname,(cljs.core.truth_(attributes)?webfui.html.render_attributes.call(null,attributes):null),cljs.core.map.call(null,html,children));
} else
{return "";
}
} else
{if("\uFDD0'else")
{return [cljs.core.str(content)].join('');
} else
{return null;
}
}
});
webfui.html.html_delta = (function html_delta(old_html,new_html,path){
if(cljs.core._EQ_.call(null,cljs.core.count.call(null,old_html),cljs.core.count.call(null,new_html)))
{var pairs = cljs.core.map.call(null,cljs.core.vector,old_html,new_html);
var fixable = cljs.core.every_QMARK_.call(null,(function (p__31217){
var vec__31218 = p__31217;
var old_child = cljs.core.nth.call(null,vec__31218,0,null);
var new_child = cljs.core.nth.call(null,vec__31218,1,null);
if((function (){var and__3941__auto__ = cljs.core.instance_QMARK_.call(null,webfui.html.parsed_html,old_child);
if(and__3941__auto__)
{return cljs.core.instance_QMARK_.call(null,webfui.html.parsed_html,new_child);
} else
{return and__3941__auto__;
}
})())
{return cljs.core._EQ_.call(null,old_child.tagname,new_child.tagname);
} else
{return cljs.core._EQ_.call(null,old_child,new_child);
}
}),pairs);
if(fixable)
{return cljs.core.apply.call(null,cljs.core.concat,cljs.core.map_indexed.call(null,(function (i,p__31219){
var vec__31220 = p__31219;
var old_element = cljs.core.nth.call(null,vec__31220,0,null);
var new_element = cljs.core.nth.call(null,vec__31220,1,null);
if(cljs.core.instance_QMARK_.call(null,webfui.html.parsed_html,old_element))
{var old_tagname = old_element.tagname;
var old_attributes = old_element.attributes;
var old_children = old_element.children;
var new_tagname = new_element.tagname;
var new_attributes = new_element.attributes;
var new_children = new_element.children;
var path__$1 = cljs.core.conj.call(null,path,i);
var att_delta = ((cljs.core.not_EQ_.call(null,old_attributes,new_attributes))?cljs.core.mapcat.call(null,(function (key){
var old_val = old_attributes.call(null,key);
var new_val = new_attributes.call(null,key);
if(cljs.core.not.call(null,new_val))
{return cljs.core.PersistentVector.fromArray([cljs.core.PersistentVector.fromArray(["\uFDD0'rem-att",path__$1,key], true)], true);
} else
{if(cljs.core.not_EQ_.call(null,old_val,new_val))
{return cljs.core.PersistentVector.fromArray([cljs.core.PersistentVector.fromArray(["\uFDD0'att",path__$1,key,new_val], true)], true);
} else
{if("\uFDD0'else")
{return cljs.core.PersistentVector.EMPTY;
} else
{return null;
}
}
}
}),clojure.set.union.call(null,cljs.core.set.call(null,cljs.core.keys.call(null,old_attributes)),cljs.core.set.call(null,cljs.core.keys.call(null,new_attributes)))):null);
var child_delta = html_delta.call(null,old_children,new_children,path__$1);
return cljs.core.concat.call(null,att_delta,child_delta);
} else
{return null;
}
}),pairs));
} else
{return cljs.core.PersistentVector.fromArray([cljs.core.PersistentVector.fromArray(["\uFDD0'html",path,new_html], true)], true);
}
} else
{return cljs.core.PersistentVector.fromArray([cljs.core.PersistentVector.fromArray(["\uFDD0'html",path,new_html], true)], true);
}
});
webfui.html.unparse_html = (function unparse_html(html){
if((function (){var or__3943__auto__ = cljs.core.string_QMARK_.call(null,html);
if(or__3943__auto__)
{return or__3943__auto__;
} else
{return cljs.core.number_QMARK_.call(null,html);
}
})())
{return html;
} else
{var tagname = html.tagname;
var attributes = html.attributes;
var children = html.children;
return cljs.core.vec.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.fromArray([tagname,attributes], true),cljs.core.map.call(null,unparse_html,children)));
}
});
