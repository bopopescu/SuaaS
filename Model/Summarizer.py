import nltk

from nltk.tokenize import sent_tokenize, word_tokenize
from string import punctuation
from collections import defaultdict
from heapq import nlargest

stopwords = list(
    ["i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves",
     "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their",
     "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was",
     "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the",
     "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against",
     "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in",
     "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why",
     "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only",
     "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"])


class FrequencySummarizer:
    def __init__(self, minCut=0.1, maxCut=0.9):
        self._minCut = minCut
        self._maxCut = maxCut
        self._stopwords = set(stopwords + list(punctuation))

    # This function returns the values for
    def _compute_frequencies(self, tokenizedSentences):
        freq = defaultdict(int)
        for word_tokens in tokenizedSentences:
            for word in word_tokens:
                if word not in self._stopwords:
                    freq[word] += 1
        ###################################################
        # Normalising the values
        ###################################################
        m = float(max(freq.values()))
        for word in list(freq):
            freq[word] = freq[word] / m
            if freq[word] >= self._maxCut or freq[word] <= self._minCut:
                del freq[word]
        return freq

    # Summarizer
    def summarize(self, text, n):
        sents = sent_tokenize(text)
        if n >= len(sents):
            return sents
        tokenizedSentence = [word_tokenize(s.lower()) for s in sents]
        self._freq = self._compute_frequencies(tokenizedSentence)
        ranking = defaultdict(int)
        for i, sent in enumerate(tokenizedSentence):
            for w in sent:
                if w in self._freq:
                    ranking[i] += self._freq[w]
        sents_idx = self._rank(ranking, n)
        return [sents[j] for j in sents_idx]

    # N ranked sentences
    def _rank(self, ranking, n):
        return nlargest(n, ranking, key=ranking.get)
