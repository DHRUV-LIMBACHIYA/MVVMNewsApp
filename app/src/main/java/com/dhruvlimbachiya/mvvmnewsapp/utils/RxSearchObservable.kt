package com.dhruvlimbachiya.mvvmnewsapp.utils

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */

fun rxSearchObservable(editText: EditText): Observable<String> {

    val subject = PublishSubject.create<String>()

    editText.addTextChangedListener { editable ->
        editable?.let {
            subject.onNext(editable.toString())
        }
    }

    return subject
}